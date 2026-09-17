package com.skushwaha.getform.HomePage.DTO;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data


public class ContactMessageRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 80, message = "First name must be at most 80 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 80, message = "Last name must be at most 80 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 160, message = "Email must be at most 160 characters")
    private String email;

    @Size(max = 30, message = "Phone number is too long")
    private String phone;

    @NotBlank(message = "Subject is required")
    @Size(max = 60, message = "Subject must be at most 60 characters")
    private String subject;

    @NotBlank(message = "Message is required")
    @Size(max = 5000, message = "Message must be at most 5000 characters")
    private String message;

}
