package com.skushwaha.getform.Auth.DTO;

import com.skushwaha.getform.Users.Entity.Role;

public record AuthResponse(

        String accessToken,

        String email,

        String name,

        Role role,


        String tokenType,

        long expiresIn
) {
}
