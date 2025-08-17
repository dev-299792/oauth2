package com.example.authserver.security.authentication;

import com.example.authserver.entity.AuthorizationCode;
import com.example.authserver.repository.AuthorizationCodeRepository;
import com.example.authserver.util.HashUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Custom {@link AuthenticationProvider} for handling PKCE (Proof Key for Code Exchange) validation.
 *
 * Validates a {@link PkceAutheticationToken} by comparing the provided code verifier
 * against the stored code challenge for the associated {@link AuthorizationCode}.
 *
 * @see AuthorizationCodeRepository
 * @see AuthorizationCode
 * @see PkceAutheticationToken
 */
@Component
@AllArgsConstructor
public class PkceAuthenticationProvider implements AuthenticationProvider {

    private final AuthorizationCodeRepository authorizationCodeRepository;

    /**
     * Authenticates a PKCE request.
     *
     * Retrieves the {@link AuthorizationCode} from the repository, generates a code challenge
     * from the provided verifier (SHA256 or plain), and compares it with the stored challenge.
     *
     * @param authentication the PKCE authentication request
     * @return a valid {@link PkceAutheticationToken} if verification succeeds
     * @throws AuthenticationException if verification fails
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        PkceAutheticationToken token = (PkceAutheticationToken) authentication;
        AuthorizationCode code = authorizationCodeRepository.findById(token.getAuthorizationCode())
                .orElse(null);

        if (code != null && code.getCodeChallenge() != null) {
            String generatedChallenge = null;
            String method = code.getCodeChallengeMethod();
            if ("SHA256".equalsIgnoreCase(method)) {
                generatedChallenge = HashUtil.generateSha256Base64Encoded(token.getCodeVerifier());

            } else if ("plain".equalsIgnoreCase(method)) {
                generatedChallenge = token.getCodeVerifier();
            }
            if (generatedChallenge != null && generatedChallenge.equals(code.getCodeChallenge())) {
                return new PkceAutheticationToken(code.getClient().getClientId());
            }
        }
        throw new BadCredentialsException("invalid_pkce_request.");
    }

    /**
     * Checks if this provider supports PKCE authentication.
     *
     * @param authentication the authentication class
     * @return true if the type is {@link PkceAutheticationToken}
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return PkceAutheticationToken.class.isAssignableFrom(authentication);
    }
}
