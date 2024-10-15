package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.events.SnsEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import com.syndicate.deployment.model.events.SnsTriggerEventSourceItem;
import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
    lambdaName = "sns_handler",
    roleName = "sns_handler-role",
    isPublishVersion = true,
    aliasName = "${lambdas_alias_name}",
    logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@SnsEventSource(targetTopic = "lambda_topic")
public class SnsHandler implements RequestHandler<SnsTriggerEventSourceItem, Void> {

  public Void handleRequest(SnsTriggerEventSourceItem event, Context context) {
    LambdaLogger logger = context.getLogger();
    logger.log(event.toString());
    return null;
  }
}
