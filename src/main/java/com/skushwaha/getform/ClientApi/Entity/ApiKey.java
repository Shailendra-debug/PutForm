package com.skushwaha.getform.ClientApi.Entity;

import com.skushwaha.getform.Users.Entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "api_keys",
        indexes = {
                @Index(
                        name = "idx_api_keys_hash",
                        columnList = "key_hash"
                )
        }
)
@Getter
@Setter
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "key_hash",
            nullable = false,
            unique = true,
            length = 64
    )
    private String keyHash;

    @Column(
            name = "key_prefix",
            nullable = false,
            length = 8
    )
    private String keyPrefix;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private LocalDateTime lastUsedAt;

    private LocalDateTime revokedAt;
}