package com.example.authserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * VerificationToken entity.
 *
 * <p>Stores verification token for user account verification
 */
@Entity
@Table(name = "verification_tokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken {

    @Id
    @Column(name = "id", length = 100)
    private String id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    private LocalDateTime expiresAt;
    private LocalDateTime createdAt = LocalDateTime.now();
    private boolean used = false;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        expiresAt = createdAt.plusMinutes(15);
    }
}

