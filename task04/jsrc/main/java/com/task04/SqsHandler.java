package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.events.SqsTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.events.SqsTriggerEventSourceItem;

@LambdaHandler(
    lambdaName = "sqs_handler",
    roleName = "sqs_handler-role",
    isPublishVersion = true,
    aliasName = "${lambdas_alias_name}",
    logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@SqsTriggerEventSource(targetQueue = "async_queue", batchSize = 3)
public class SqsHandler implements RequestHandler<Object, Void> {

  public Void handleRequest(Object event, Context context) {
    LambdaLogger logger = context.getLogger();
    logger.log(event.toString());
    return null;
  }
}
