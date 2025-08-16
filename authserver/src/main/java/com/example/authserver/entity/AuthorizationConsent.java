package com.example.authserver.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;


/**
 * Represents a consent for an authorization grant.
 * Consent is a record of the user's decision to grant access to the client
 * for a specific set of scopes.
 *
 */
@Entity
@Table(name = "consents")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationConsent {

    @Id
    @Column(length = 100)
    private String id;

    @Column(name = "user_id", length = 100, nullable = false)
    private String userId;

    @Column(name = "client_id", length = 100, nullable = false)
    private String clientId;

    @Column(length = 1000, nullable = false)
    private String scope;

    @Column(name = "granted_at", nullable = false)
    private LocalDateTime grantedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean active = true;

    /**
     * Sets the current time as the grantedAt value if it is null.
     * This method is called before the entity is persisted.
     */
    @PrePersist
    public void prePersist() {
        if (grantedAt == null) {
            grantedAt = LocalDateTime.now();
        }
    }

    public Set<String> getScopesSet() {
        return stringToSet(this.scope);
    }

    public void setScopesSet(Set<String> scopes) {
        this.scope = collectionToString(scopes);
        if (this.scope != null && this.scope.isEmpty()) {
            this.scope = null;
        }
    }

    private Set<String> stringToSet(String delimitedString) {
        String[] stringArray = StringUtils.delimitedListToStringArray(delimitedString," ");
        return Set.of(stringArray);
    }

    private String collectionToString(Collection<String> collection) {
        return StringUtils.collectionToDelimitedString(collection," ");
    }

}
