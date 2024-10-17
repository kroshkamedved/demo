package com.task07;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.events.RuleEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.events.RuleEventSourceItem;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

@LambdaHandler(
    lambdaName = "uuid_generator",
    roleName = "uuid_generator-role",
    isPublishVersion = true,
    aliasName = "${lambdas_alias_name}",
    logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@RuleEventSource(targetRule = "uuid_trigger")
@EnvironmentVariable(key = "bucket", value = "${notification_bucket}")
public class UuidGenerator implements RequestHandler<RuleEventSourceItem, Void> {

  private final AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
  ;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public Void handleRequest(RuleEventSourceItem request, Context context) {
    OutputStream outputStream = new ByteArrayOutputStream();

    s3Client.putObject(System.getenv("bucket"), "test", "some TEXT");
    return null;
  }
}
