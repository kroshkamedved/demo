package com.task07;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.events.RuleEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.events.RuleEventSourceItem;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@LambdaHandler(
    lambdaName = "uuid_generator",
    roleName = "uuid_generator-role",
    isPublishVersion = true,
    aliasName = "${lambdas_alias_name}",
    logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@RuleEventSource(targetRule = "uuid_trigger")
@EnvironmentVariable(key = "bucket", value = "${target_bucket}")
public class UuidGenerator implements RequestHandler<RuleEventSourceItem, Void> {

  private final AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();

  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  private final ObjectMapper objectMapper = new ObjectMapper();

  public Void handleRequest(RuleEventSourceItem request, Context context) {
    OutputStream outputStream = new ByteArrayOutputStream();
    List<String> list = new ArrayList<>(10);
    int iter = 0;
    while (iter < 10) {
      iter++;
      list.add(UUID.randomUUID().toString());
    }
    Map<String, Object> map = new HashMap<>();
    map.put("ids", list);
    s3Client.putObject(System.getenv("bucket"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(
            ZonedDateTime.now()), gson.toJson(map));
    return null;
  }
}
