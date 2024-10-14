package com.mitar.dipl.model.dto.login;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LoginRequest {

    @Email
    private String email;

    @NotBlank(message = "Password is mandatory.")
    @NotEmpty(message = "Password cannot be empty")
    @Min(value = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit")
    private String password;

}
