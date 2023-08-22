package ru.viterg.proselyte.stocksfeed.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.viterg.proselyte.stocksfeed.user.Role;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {

  private String email;
  private Role role;
}
