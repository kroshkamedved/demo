package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
    lambdaName = "hello_world",
    roleName = "hello_world-role",
    isPublishVersion = true,
    aliasName = "${lambdas_alias_name}",
    logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig(authType = AuthType.NONE)
public class HelloWorld implements RequestHandler<APIGatewayV2HTTPEvent, Map<String, Object>> {

  @Override
  public Map<String, Object> handleRequest(APIGatewayV2HTTPEvent requestEvent, Context context) {
    String path = requestEvent.getRequestContext().getHttp().getPath();
    String method = requestEvent.getRequestContext().getHttp().getMethod();
    Map<String, Object> resultMap = new HashMap<>();
    if (path.equals("/hello")) {
      resultMap.put("statusCode", 200);
      resultMap.put("message", "Hello from Lambda");
    } else {
      resultMap.put("statusCode", 400);
      resultMap.put("message",
          "Bad request syntax or unsupported method. Request path: " + path
              + ". HTTP method: " + method);
    }
    return resultMap;
  }
}
