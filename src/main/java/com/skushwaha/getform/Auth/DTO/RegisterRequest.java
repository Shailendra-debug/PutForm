package com.skushwaha.getform.Auth.DTO;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Name is required")
         String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        String email,



        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100,
                message = "Password must contain 8-100 characters")
        String password
) {
}