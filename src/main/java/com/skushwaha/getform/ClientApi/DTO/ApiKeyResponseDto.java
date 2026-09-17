package com.skushwaha.getform.ClientApi.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiKeyResponseDto {

    private Long id;
    private String name;           // e.g., "Production Web App"
    private String keyPrefix;      // e.g., "gf_live_4a8f..."
    private boolean active;
    private LocalDateTime lastUsedAt;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}