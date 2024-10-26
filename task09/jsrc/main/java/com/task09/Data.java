package com.task09;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@lombok.Data
@DynamoDbBean
public class Data {

  private Double latitude;
  private Double longitude;
  private String timezone;
  private String timezone_abbreviation;
  private Double elevation;
  private Long utc_offset_seconds;
  private Hourly hourly;
  private Units hourly_units;
  private Double generationtime_ms;

  // Getters and Setters
  // Constructor
}