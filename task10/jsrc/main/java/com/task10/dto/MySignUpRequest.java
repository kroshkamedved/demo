package com.task10.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MySignUpRequest {

  private String firstName;
  private String lastName;
  private String email;
  private String password;
}
