package com.task06;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.events.DynamoDbTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.ResourceType;
import com.syndicate.deployment.model.RetentionSetting;
import com.task06.dto.Request;
import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
    lambdaName = "audit_producer",
    roleName = "audit_producer-role",
    isPublishVersion = true,
    aliasName = "${lambdas_alias_name}",
    logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@DynamoDbTriggerEventSource(targetTable = "Configuration", batchSize = 1)
@DependsOn(name = "Configuration", resourceType = ResourceType.DYNAMODB_TABLE)
@EnvironmentVariable(key = "TABLE", value = "${target_table}")
public class AuditProducer implements RequestHandler<DynamodbEvent, Void> {

  private final Regions region = Regions.EU_CENTRAL_1;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private AmazonDynamoDB amazonDynamoDB;
  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  public Void handleRequest(DynamodbEvent request, Context context) {
    String json = gson.toJson(request);
    try {
      context.getLogger().log(request.toString());
      Request entity = objectMapper.readValue(json, Request.class);
      context.getLogger().log(entity.toString());
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    String tableName = System.getenv("TABLE");
    Map<String, AttributeValue> resultMap = new HashMap<>();
    initDynamoDbClient();
    amazonDynamoDB.putItem(tableName, resultMap);
    return null;
  }

  private void initDynamoDbClient() {
    this.amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
        .withRegion(region)
        .build();
  }
}
