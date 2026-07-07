package net.javaguides.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AuthUserResponse {

  private final String subject;
  private final String email;
  private final String name;
  private final List<String> roles;
}
