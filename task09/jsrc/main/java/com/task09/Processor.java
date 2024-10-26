package com.task09;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

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
  private final ObjectMapper objectMapper = new ObjectMapper();

  public Void handleRequest(Object request, Context context) {
    initDynamoDbClient();
    try (CloseableHttpClient client = HttpClients.createDefault()) {
      String url = "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,wind_speed_10m&hourly=temperature_2m,relative_humidity_2m,wind_speed_10m";
      HttpGet restRequest = new HttpGet(url);
      String jsonResponse = EntityUtils.toString(client.execute(restRequest).getEntity());
      System.out.println(objectMapper.readValue(jsonResponse, Map.class));
      Map<String, Object> map = objectMapper.readValue(jsonResponse, Map.class);
      Map<String, AttributeValue> attributeValueMap = new HashMap<>();
      for (Map.Entry<String, Object> entry : map.entrySet()) {
        attributeValueMap.put(entry.getKey(), new AttributeValue(entry.getValue().toString()));
      }
      Map<String, AttributeValue> resultMap = new HashMap<>();
      resultMap.put("id", new AttributeValue(UUID.randomUUID().toString()));
      resultMap.put("forecast", new AttributeValue().withM(attributeValueMap));
      String tableName = System.getenv("table");
      amazonDynamoDB.putItem(tableName, resultMap);
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
