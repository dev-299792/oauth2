package com.example.authserver.dto;

import com.example.authserver.entity.AccessToken;
import lombok.*;

import java.time.Duration;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessTokenResponseDTO {

    private String access_token;
    private Long expires_in; // Use Long for expires_in as it's a duration in seconds
    private String refresh_token;
    private Long refresh_token_expires_in;
    private String scope;
    private String tokenType;

    public static AccessTokenResponseDTO getDTO(AccessToken token) {

        Duration accessTokenExp = Duration.between(token.getCreatedAt(), token.getExpiresAt());
        Duration refreshTokenExp = Duration.between(token.getCreatedAt(), token.getRefreshTokenExpiresAt());

        return AccessTokenResponseDTO
                .builder()
                .access_token(token.getToken())
                .refresh_token(token.getRefreshToken())
                .scope(token.getScopes())
                .expires_in(accessTokenExp.getSeconds())
                .refresh_token_expires_in(refreshTokenExp.getSeconds())
                .tokenType("Bearer") // ToDo: remove hardcode token typed
                .build();
    }

}
