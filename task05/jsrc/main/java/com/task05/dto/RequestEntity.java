package com.task05.dto;

import java.util.Map;

public class RequestEntity {

  private Long principalId;
  private Map<String, String> content;

  public RequestEntity(Long principalId, Map<String, String> content) {
    this.principalId = principalId;
    this.content = content;
  }

  public RequestEntity() {
  }

  public void setPrincipalId(Long principalId) {
    this.principalId = principalId;
  }

  public void setContent(Map<String, String> content) {
    this.content = content;
  }

  public Long getPrincipalId() {
    return principalId;
  }

  public Map<String, String> getContent() {
    return content;
  }

  @Override
  public String toString() {
    return "RequestEntity{" +
        "principalId=" + principalId +
        ", content=" + content +
        '}';
  }
}
