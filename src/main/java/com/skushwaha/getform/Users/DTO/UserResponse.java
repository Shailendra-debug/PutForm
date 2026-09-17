package com.skushwaha.getform.Users.DTO;

import com.skushwaha.getform.Users.Entity.Role;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String profileUrl;
    private Role role;
    private boolean enabled;
    private BigDecimal walletBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
