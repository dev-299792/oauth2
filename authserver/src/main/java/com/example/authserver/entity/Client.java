package com.example.authserver.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.Collection;

@Entity
@Table(name = "client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @Column(name = "id", length = 100)
    private String id;

    @Column(name = "client_id", length = 100, nullable = false, unique = true)
    private String clientId;

    @CreationTimestamp
    @Column(name = "client_id_issued_at", nullable = false, updatable = false)
    private LocalDateTime clientIdIssuedAt;

    @Column(name = "client_secret", length = 200)
    private String clientSecret;

    @Column(name = "client_secret_expires_at")
    private LocalDateTime clientSecretExpiresAt;

    @Column(name = "client_name", length = 200, nullable = false)
    private String clientName;

    @Column(name = "client_authentication_methods", length = 1000, nullable = false)
    private String clientAuthenticationMethods;

    @Column(name = "authorization_grant_types", length = 1000, nullable = false)
    private String authorizationGrantTypes;

    @Column(name = "redirect_uris", length = 1000)
    private String redirectUris;

    @Column(name = "scopes", length = 1000)
    private String scopes;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    public Set<String> getClientAuthenticationMethodsSet() {
        return stringToSet(this.clientAuthenticationMethods);
    }

    public void setClientAuthenticationMethodsSet(Set<String> clientAuthenticationMethods) {
        this.clientAuthenticationMethods = collectionToString(clientAuthenticationMethods);
    }

    public Set<String> getAuthorizationGrantTypesSet() {
        return stringToSet(this.authorizationGrantTypes);
    }

    public void setAuthorizationGrantTypesSet(Set<String> authorizationGrantTypes) {
        this.authorizationGrantTypes = collectionToString(authorizationGrantTypes);
    }

    public Set<String> getRedirectUrisSet() {
        return stringToSet(this.redirectUris);
    }

    public void setRedirectUrisSet(Set<String> redirectUris) {
        this.redirectUris = collectionToString(redirectUris);
        if (this.redirectUris != null && this.redirectUris.isEmpty()) {
            this.redirectUris = null;
        }
    }

    public Set<String> getScopesSet() {
        return stringToSet(this.scopes);
    }

    public void setScopesSet(Set<String> scopes) {
        this.scopes = collectionToString(scopes);
        if (this.scopes != null && this.scopes.isEmpty()) {
            this.scopes = null;
        }
    }

    private Set<String> stringToSet(String delimitedString) {
        return StringUtils.commaDelimitedListToSet(delimitedString);
    }

    private String collectionToString(Collection<String> collection) {
        return StringUtils.collectionToCommaDelimitedString(collection);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id='" + id + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientName='" + clientName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id); // Use Objects.equals for null-safe comparison
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Use Objects.hash for null-safe hash code generation
    }
}