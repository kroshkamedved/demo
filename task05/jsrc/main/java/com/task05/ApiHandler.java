package com.task05;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import com.task05.dto.RequestEntity;
import com.task05.model.Event;
import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
    lambdaName = "api_handler",
    roleName = "api_handler-role",
    isPublishVersion = true,
    aliasName = "${lambdas_alias_name}",
    logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@EnvironmentVariable(key = "TABLE", value = "${target_table}")
public class ApiHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

  private final Regions region = Regions.EU_CENTRAL_1;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private AmazonDynamoDB amazonDynamoDB;
  private static final int SC_OK = 200;
  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
  private final Map<String, String> responseHeaders = Map.of("Content-Type", "application/json");

  public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent request, Context context) {
    String tableName = System.getenv("TABLE");
    context.getLogger().log(tableName);
    try {
      this.initDynamoDbClient();
      context.getLogger().log(request.getBody());
      RequestEntity entity = objectMapper.readValue(request.getBody(), RequestEntity.class);
      Event event = new Event(entity.getPrincipalId(), entity.getContent());
      persistData(event);
      context.getLogger().log(event.toString());
      return buildResponse(SC_OK, event);
    } catch (JsonProcessingException e) {
      context.getLogger().log("EXCEPTION DURING BODY DESERIALIZATION: body:" + request.getBody());
      throw new RuntimeException(e);
    }
  }

  private APIGatewayV2HTTPResponse buildResponse(int statusCode, Event event) {
    HashMap<String, Object> responseBody = new HashMap<>();
    responseBody.put("statusCode", statusCode);
    responseBody.put("event", event);
    return APIGatewayV2HTTPResponse.builder()
        .withStatusCode(statusCode)
        .withHeaders(responseHeaders)
        .withBody(gson.toJson(responseBody))
        .build();
  }

  private void persistData(Event event)
      throws ConditionalCheckFailedException {
    DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
    dynamoDBMapper.save(event);
  }

  private void initDynamoDbClient() {
    this.amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
        .withRegion(region)
        .build();
  }
}
