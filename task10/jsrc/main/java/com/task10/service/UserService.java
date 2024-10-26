package com.task10.service;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.task10.dto.MySignUpRequest;
import com.task10.dto.SignInRequest;
import java.util.Map;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeliveryMediumType;

@EnvironmentVariable(key = "bookingUserPool", value = "booking_userpool")
public class UserService {

  Gson gson = new Gson();


  public APIGatewayProxyResponseEvent processSignup(APIGatewayProxyRequestEvent request,
      String functionName, Context context) {
    return handleSignupRequest(request, functionName, context);
  }

  public APIGatewayProxyResponseEvent processSignin(APIGatewayProxyRequestEvent request,
      String functionName) {
    return handleSigninRequest(request, functionName);
  }

  public APIGatewayProxyResponseEvent handleSignupRequest(
      APIGatewayProxyRequestEvent requestEvent, String functionName, Context context) {
    var request = gson.fromJson(requestEvent.getBody(), MySignUpRequest.class);
    System.out.println("UserSignupRequest = " + request);

    var cognitoClient = CognitoIdentityProviderClient.create();
    var userPoolId = System.getenv("COGNITO_ID");
    var clientId = System.getenv("CLIENT_ID");
    try {
      var createUserRequest = AdminCreateUserRequest.builder()
          .userPoolId(userPoolId)
          .temporaryPassword(request.getPassword())
          .userAttributes(
              AttributeType.builder()
                  .name("given_name")
                  .value(request.getFirstName())
                  .build(),
              AttributeType.builder()
                  .name("family_name")
                  .value(request.getLastName())
                  .build(),
              AttributeType.builder()
                  .name("email")
                  .value(request.getEmail())
                  .build(),
              AttributeType.builder()
                  .name("email_verified")
                  .value("true")
                  .build())
          .desiredDeliveryMediums(DeliveryMediumType.EMAIL)
          .username(request.getEmail())
          .messageAction("SUPPRESS")
          .build();

      var setUserPasswordRequest = AdminSetUserPasswordRequest.builder()
          .userPoolId(userPoolId)
          .username(request.getEmail())
          .password(request.getPassword())
          .permanent(true)
          .build();

      var createUserResponse = cognitoClient.adminCreateUser(createUserRequest);
      var setUserPasswordResponse = cognitoClient.adminSetUserPassword(setUserPasswordRequest);
      System.out.println("createUserResponse = " + createUserResponse);
      context.getLogger().log("createUserResponse = " + createUserResponse);
      context.getLogger().log("setUserPasswordResponse = " + setUserPasswordResponse);
      System.out.println("setUserPasswordResponse = " + setUserPasswordResponse);

    } catch (Exception e) {
      context.getLogger().log("exception = " + e.getMessage());
      throw new RuntimeException(e);
    }

    var apiGatewayResponse = new APIGatewayProxyResponseEvent();
    apiGatewayResponse.setStatusCode(200);
    apiGatewayResponse.setHeaders(Map.of("Content-Type", "application/json"));
    return apiGatewayResponse;
  }

  public APIGatewayProxyResponseEvent handleSigninRequest(
      APIGatewayProxyRequestEvent requestEvent, String functionName) {
    var request = gson.fromJson(requestEvent.getBody(), SignInRequest.class);
    System.out.println("UserSigninRequest = " + request);

    var cognitoClient = CognitoIdentityProviderClient.create();
    var userPoolId = System.getenv("COGNITO_ID");
    var clientId = System.getenv("CLIENT_ID");

    var authParams = Map.of(
        "USERNAME", request.getEmail(),
        "PASSWORD", request.getPassword()
    );

    var authRequest = AdminInitiateAuthRequest.builder()
        .authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
        .userPoolId(userPoolId)
        .clientId(clientId)
        .authParameters(authParams)
        .build();

    var authResponse = cognitoClient.adminInitiateAuth(authRequest);
    System.out.println("Auth response: " + authResponse);
    System.out.println("ID Token: " + authResponse.authenticationResult().idToken());
    System.out.println("Access Token: " + authResponse.authenticationResult().accessToken());
    System.out.println("Refresh Token: " + authResponse.authenticationResult().refreshToken());
    var apiGatewayResponse = new APIGatewayProxyResponseEvent();
    apiGatewayResponse.setStatusCode(200);
    apiGatewayResponse.setHeaders(Map.of("Content-Type", "application/json"));
    String token = authResponse.authenticationResult().idToken();
    apiGatewayResponse.setBody(token);

    return apiGatewayResponse;
  }


}
