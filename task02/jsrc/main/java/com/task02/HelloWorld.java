package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
public class HelloWorld implements
    RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

  private static final int SC_OK = 200;
  private static final int SC_NOT_FOUND = 404;
  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
  private final Map<String, String> responseHeaders = Map.of("Content-Type", "application/json");

  public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
    Map<String, Object> resultMap = new HashMap<>();
    if ("/hello".equals(getPath(event))) {
      resultMap.put("statusCode", 200);
      resultMap.put("message", "Hello from Lambda");
      return buildResponse(SC_NOT_FOUND, "successful");

    } else {
      resultMap.put("statusCode", 400);
      resultMap.put("message",
          "Bad request syntax or unsupported method. Request path: " + getPath(event)
              + ". HTTP method: " + getMethod(event));

      return buildResponse(SC_OK, "unsuccessful");
    }
  }

  private APIGatewayV2HTTPResponse buildResponse(int statusCode, Object body) {
    return APIGatewayV2HTTPResponse.builder()
        .withStatusCode(statusCode)
        .withHeaders(responseHeaders)
        .withBody(gson.toJson(body))
        .build();
  }

  private String getMethod(APIGatewayV2HTTPEvent requestEvent) {
    return requestEvent.getRequestContext().getHttp().getMethod();
  }

  private String getPath(APIGatewayV2HTTPEvent requestEvent) {
    return requestEvent.getRequestContext().getHttp().getPath();
  }
}
