package com.task09;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@lombok.Data
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
public class Hourly {

  private List<String> time;
  private List<Double> temperature_2m;

}
