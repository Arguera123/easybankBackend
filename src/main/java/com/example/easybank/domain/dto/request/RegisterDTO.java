package com.example.easybank.domain.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterDTO {

    @NotBlank(message = "DUI is required")
    @Pattern(
            regexp = "^\\d{8}-\\d$",
            message = "DUI must contain 9 digits"
    )
    private String dui;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9_.-]+$",
            message = "Username may only contain letters, numbers, dots, underscores and hyphens"
    )
    private String username;

    @NotBlank(message = "First name is required")
    @Size(max = 30, message = "First name cannot exceed 30 characters")
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$",
            message = "First name can only contain letters and spaces"
    )
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 30, message = "Last name cannot exceed 30 characters")
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$",
            message = "Last name can only contain letters and spaces"
    )
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "Password must contain at least one number, one lowercase letter, one uppercase letter, and one special character"
    )
    private String password;
}

