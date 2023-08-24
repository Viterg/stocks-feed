package ru.viterg.proselyte.stocksfeed.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Size(min = 3)
    private String username;
    @NotBlank
    @Size(min = 4)
    private String password;
    @Email
    @NotBlank
    private String email;
}
