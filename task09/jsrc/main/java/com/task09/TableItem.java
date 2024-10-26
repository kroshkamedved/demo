package com.task09;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@DynamoDbBean
@AllArgsConstructor
@NoArgsConstructor
public class TableItem {

  private com.task09.Data forecast;
  private String id;

  @DynamoDbPartitionKey
  public String getId() {
    return this.id;
  }
}
