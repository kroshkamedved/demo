package com.task10;

import static com.syndicate.deployment.model.environment.ValueTransformer.USER_POOL_NAME_TO_CLIENT_ID;
import static com.syndicate.deployment.model.environment.ValueTransformer.USER_POOL_NAME_TO_USER_POOL_ID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import com.task10.service.ReservationService;
import com.task10.service.TableService;
import com.task10.service.UserService;
import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
    lambdaName = "api_handler",
    roleName = "api_handler-role",
    isPublishVersion = true,
    aliasName = "${lambdas_alias_name}",
    logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@EnvironmentVariables(value = {
    @EnvironmentVariable(key = "tablesTable", value = "${tables_table}"),
    @EnvironmentVariable(key = "reservationsTable", value = "${reservations_table}"),
    @EnvironmentVariable(key = "bookingUserPool", value = "${booking_userpool}"),
    @EnvironmentVariable(key = "COGNITO_ID", value = "${booking_userpool}", valueTransformer = USER_POOL_NAME_TO_USER_POOL_ID),
    @EnvironmentVariable(key = "CLIENT_ID", value = "${booking_userpool}", valueTransformer = USER_POOL_NAME_TO_CLIENT_ID)
})
public class ApiHandler implements
    RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  private final UserService userService = new UserService();
  private final TableService tableService = new TableService();
  private final ReservationService reservationService = new ReservationService();

  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request,
      Context context) {
    context.getLogger().log("TEST LOGGING");
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String functionName = context.getFunctionName();
    APIGatewayProxyResponseEvent responseEvent = null;
    String path = request.getRequestContext().getPath();
    String httpMethod = request.getHttpMethod();
    var requestParams = request.getPathParameters();
    try {
      context.getLogger().log("request :" + gson.toJson(request));
      if ("/signup".equals(path) && "POST".equals(httpMethod)) {
        responseEvent = userService.processSignup(request, functionName);
      } else if ("/signin".equals(path) && "POST".equals(httpMethod)) {
        responseEvent = userService.processSignin(request, functionName);
      } else if (path.startsWith("/tables") && !requestParams.isEmpty()
          && requestParams.get("tableid") != null) {
        responseEvent = tableService.getTable(request);
      } else if ("/tables".equals(path) && "GET".equals(httpMethod)) {
        responseEvent = tableService.getTables(request);
      } else if ("/tables".equals(path) && "POST".equals(httpMethod)) {
        responseEvent = tableService.addTable(request);
      } else if ("/reservations".equals(path) && "GET".equals(httpMethod)) {
        responseEvent = reservationService.getReservation(request);
      } else if ("/reservations".equals(path) && "POST".equals(httpMethod)) {
        responseEvent = reservationService.addReservation(request);
      } else {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("message", "There was an error in the request.");
        responseEvent = new APIGatewayProxyResponseEvent()
            .withStatusCode(400)
            .withBody(gson.toJson(hashMap));
      }
    } catch (Exception e) {
      context.getLogger().log("exception :" + e.getMessage());
    }
    responseEvent.setHeaders(initHeadersForCORS());
    return responseEvent;

  }

  /**
   * To allow all origins, all methods, and common headers
   * <a href="https://docs.aws.amazon.com/apigateway/latest/developerguide/how-to-cors.html">Using
   * cross-origin resource sharing (CORS)</a>
   */
  private Map<String, String> initHeadersForCORS() {
    return Map.of(
        "Access-Control-Allow-Headers",
        "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token",
        "Access-Control-Allow-Origin", "*",
        "Access-Control-Allow-Methods", "*",
        "Accept-Version", "*"
    );
  }
}
