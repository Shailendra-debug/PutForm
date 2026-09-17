package com.skushwaha.getform.Auth.DTO;

public record PendingRegistration(
        String name,
        String email,
        String password,
        String Otp
) {}