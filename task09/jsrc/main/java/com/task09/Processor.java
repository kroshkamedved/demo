package com.task09;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import java.util.UUID;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@LambdaHandler(
    lambdaName = "processor",
    roleName = "processor-role",
    isPublishVersion = true,
    aliasName = "${lambdas_alias_name}",
    logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@EnvironmentVariable(key = "table", value = "${target_table}")
public class Processor implements RequestHandler<Object, Void> {

  private final Regions region = Regions.EU_CENTRAL_1;
  private AmazonDynamoDB amazonDynamoDB;
  private DynamoDbEnhancedClient dynamoDbClient = DynamoDbEnhancedClient.create();

  private final ObjectMapper objectMapper = new ObjectMapper();

  public Void handleRequest(Object request, Context context) {
    initDynamoDbClient();
    try (CloseableHttpClient client = HttpClients.createDefault()) {
      String url = "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,wind_speed_10m&hourly=temperature_2m,relative_humidity_2m,wind_speed_10m";
      HttpGet restRequest = new HttpGet(url);
      String jsonResponse = EntityUtils.toString(client.execute(restRequest).getEntity());
      Data data = objectMapper.readValue(jsonResponse, Data.class);
      System.out.println("Data class :" + data);
      TableItem tableItem = new TableItem();
      tableItem.setData(data);
      tableItem.setId(UUID.randomUUID().toString());
      String tableName = System.getenv("table");
      DynamoDbTable<TableItem> table = dynamoDbClient.table(tableName,
          TableSchema.fromClass(TableItem.class));
      table.putItem(tableItem);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
      return null;
    }
    return null;
  }

  private void initDynamoDbClient() {
    this.amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
        .withRegion(region)
        .build();
  }
}
