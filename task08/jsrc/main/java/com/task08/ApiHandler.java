package com.task08;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import java.util.Map;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

@LambdaHandler(
    lambdaName = "api_handler",
    roleName = "api_handler-role",
    isPublishVersion = true,
    aliasName = "${lambdas_alias_name}",
    logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
public class ApiHandler implements RequestHandler<Object, Map<String, Object>> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  public Map<String, Object> handleRequest(Object request, Context context) {
    try (CloseableHttpClient client = HttpClients.createDefault()) {
      String url = "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,wind_speed_10m&hourly=temperature_2m,relative_humidity_2m,wind_speed_10m";
      HttpGet restRequest = new HttpGet(url);
      String jsonResponse = EntityUtils.toString(client.execute(restRequest).getEntity());
      System.out.println(jsonResponse);
      return objectMapper.readValue(jsonResponse, Map.class);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
      return null;
    }
  }
}