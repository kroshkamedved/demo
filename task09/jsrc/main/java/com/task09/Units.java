package com.task09;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@lombok.Data
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
public class Units {

  private String time;
  private String temperature_2m;
  // Getters and Setters
  // Constructor
}