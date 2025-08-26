package com.example.authserver.services.impl;

import com.example.authserver.dto.ConsentDTO;
import com.example.authserver.entity.AuthorizationConsent;
import com.example.authserver.entity.Client;
import com.example.authserver.entity.User;
import com.example.authserver.exception.InvalidRequestException;
import com.example.authserver.repository.AccessTokenRepository;
import com.example.authserver.repository.AuthorizationConsentRepository;
import com.example.authserver.repository.ClientRepository;
import com.example.authserver.services.AuthorizationConsentService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of the AuthorizationConsentService.
 * This class handles user consents for client scopes, including checking existing consents,
 * retrieving new requested scopes, and saving/updating user consents.
 */
@Service
@AllArgsConstructor
public class AuthorizationConsentServiceImpl implements AuthorizationConsentService {

    private final AuthorizationConsentRepository consentRepository;
    private final ClientRepository clientRepository;
    private final AccessTokenRepository accessTokenRepository;

    /**
     * Checks whether the authenticated user has already provided consent for a given client and scope.
     *
     * @param clientId the ID of the client
     * @param scope the requested scope
     * @return true if all requested scopes are already consented, false otherwise
     */
    public boolean consentExistsForScope(String clientId,
                                         String scope) {
        return getNewRequestedScopes(clientId,scope).isEmpty();
    }

    /**
     * Returns the set of scopes requested by a client that the authenticated user has not yet consented to.
     *
     * @param clientId the ID of the client
     * @param scope the requested scope(s) as a space-delimited string
     * @return a set of scopes that require new consent
     */
    public Set<String> getNewRequestedScopes(String clientId,
                                              String scope) {

        AuthorizationConsent consent = consentRepository
                .findByUserIdAndClientId(getAuthenticatedUser().getUser_id(),clientId);

        Set<String> requestScopes = stringToSet(scope);

        if(consent!=null && consent.getScopesSet()!=null) {
            requestScopes.removeAll(consent.getScopesSet());
        }

        return requestScopes;
    }

    /**
     * Saves or updates user consent for a client and the requested scopes.
     *
     * @param clientId the ID of the client
     * @param scope the requested scope(s) as a space-delimited string
     * @return the saved or updated AuthorizationConsent entity
     * @throws InvalidRequestException if the client does not exist
     */
    @Transactional
    public AuthorizationConsent saveConsent(String clientId,
                                             String scope) {

        Client client = clientRepository.findClientByClientId(clientId)
                    .orElseThrow(()->new InvalidRequestException("Invalid client."));

        AuthorizationConsent consent = consentRepository
                .findByUserIdAndClientId(getAuthenticatedUser().getUser_id(),clientId);

        if(consent == null) {
            consent = AuthorizationConsent.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(getAuthenticatedUser().getUser_id())
                    .clientId(clientId)
                    .scope(scope)
                    .build();
        } else {
            Set<String> scopeSet = new HashSet<>();
            scopeSet.addAll(consent.getScopesSet());
            scopeSet.addAll(stringToSet(scope));
            consent.setScopesSet(scopeSet);
        }
        consent = consentRepository.save(consent);
        return consent;
    }

    @Override
    public List<ConsentDTO> getClientConsents() {

        User user = getAuthenticatedUser();

        return consentRepository.findByUserId(user.getUser_id())
                .stream()
                .map(c ->
                        new ConsentDTO(c.getClientId(),c.getScopesSet()))
                .toList();

    }

    @Transactional
    @Override
    public void revokeConsents(String clientId) {
        User user = getAuthenticatedUser();
        consentRepository.deleteAllByUserIdAndClientId(user.getUser_id(), clientId);
        accessTokenRepository.deleteAllByUserIdAndClientId(user.getUser_id(), clientId);
    }

    /**
     * Converts a space-delimited string into a Set of strings.
     *
     * @param delimitedString the string containing delimited items
     * @return a Set of individual strings
     */
    private Set<String> stringToSet(String delimitedString) {
        String[] stringArray = StringUtils.delimitedListToStringArray(delimitedString," ");
        return new HashSet<>(Set.of(stringArray));
    }

    /**
     * Retrieves the currently authenticated user from the security context.
     *
     * @return the authenticated User entity
     */
    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }

}
