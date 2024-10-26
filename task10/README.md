# task10

High level project overview - business value it brings, non-detailed technical overview.

### Notice
All the technical details described below are actual for the particular
version, or a range of versions of the software.
### Actual for versions: 1.0.0

## task10 diagram

![task10](pics/task10_diagram.png)

## Lambdas descriptions

### Lambda `lambda-name`
Lambda feature overview.

### Required configuration
#### Environment variables
* environment_variable_name: description

#### Trigger event
```buildoutcfg
{
    "key": "value",
    "key1": "value1",
    "key2": "value3"
}
```
* key: [Required] description of key
* key1: description of key1

#### Expected response
```buildoutcfg
{
    "status": 200,
    "message": "Operation succeeded"
}
```
---

## Deployment from scratch
1. action 1 to deploy the software
2. action 2
...

syndicate generate lambda --name api_handler --runtime java  
syndicate generate meta cognito_user_pool --resource_name simple-booking-userpool --auto_verified_attributes email --username_attributes email
syndicate generate meta api_gateway --resource_name task10_api --deploy_stage api
syndicate generate meta api_gateway_authorizer --api_name task10_api --name cognito_authorizer --type COGNITO_USER_POOLS --provider_name simple-booking-userpool
syndicate generate meta api_gateway_resource --api_name task10_api --path /signup
syndicate generate meta api_gateway_resource --api_name task10_api --path /signin
syndicate generate meta api_gateway_resource --api_name task10_api --path /tables
syndicate generate meta api_gateway_resource --api_name task10_api --path /reservations
syndicate generate meta api_gateway_resource_method --api_name task10_api --path /signup --method POST --integration_type lambda --lambda_name api_handler --lambda_region eu-central-1 --authorization_type CUSTOM --authorizer_name cognito_authorizer
syndicate generate meta api_gateway_resource_method --api_name task10_api --path /signin --method POST --integration_type lambda --lambda_name api_handler --lambda_region eu-central-1 --authorization_type CUSTOM --authorizer_name cognito_authorizer
syndicate generate meta api_gateway_resource_method --api_name task10_api --path /tables --method POST --integration_type lambda --lambda_name api_handler --lambda_region eu-central-1 --authorization_type CUSTOM --authorizer_name cognito_authorizer
syndicate generate meta api_gateway_resource_method --api_name task10_api --path /tables --method GET --integration_type lambda --lambda_name api_handler --lambda_region eu-central-1 --authorization_type CUSTOM --authorizer_name cognito_authorizer
syndicate generate meta api_gateway_resource_method --api_name task10_api --path /reservations --method POST --integration_type lambda --lambda_name api_handler --lambda_region eu-central-1 --authorization_type CUSTOM --authorizer_name cognito_authorizer
syndicate generate meta api_gateway_resource_method --api_name task10_api --path /reservations --method GET --integration_type lambda --lambda_name api_handler --lambda_region eu-central-1 --authorization_type CUSTOM --authorizer_name cognito_authorizer
syndicate generate meta dynamodb --resource_name Tables --hash_key_name id --hash_key_type N
syndicate generate meta dynamodb --resource_name Reservations --hash_key_name id --hash_key_type S 


