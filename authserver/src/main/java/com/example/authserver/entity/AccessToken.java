package com.example.authserver.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

import lombok.*;


/**
 * AccessToken entity.
 * The AccessToken entity is used to store access tokens and their associated data.
 *
 */
@Entity
@Table(name = "access_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessToken {

    @Id
    @Column(name = "token", length = 255)
    private String token;

    @Column(name = "refresh_token", length = 255, unique = true, nullable = false)
    private String refreshToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id", nullable = false)
    private Client client;

    @Column(name = "user_id", length = 100, nullable = false)
    private String user_id;

    @Column(name = "scopes", length = 255)
    private String scopes;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "refresh_token_expires_at", nullable = false)
    private LocalDateTime refreshTokenExpiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;


    /**
     * Initializes the expiration times of the access token and refresh token
     * before persisting token to database.
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        expiresAt = createdAt.plusMinutes(5);
        refreshTokenExpiresAt = createdAt.plusMinutes(30);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessToken accessToken = (AccessToken) o;
        return Objects.equals(token, accessToken.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }

}