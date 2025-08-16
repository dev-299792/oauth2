package com.example.authserver.services.impl;

import com.example.authserver.entity.AuthorizationConsent;
import com.example.authserver.entity.Client;
import com.example.authserver.entity.User;
import com.example.authserver.exception.InvalidRequestException;
import com.example.authserver.repository.AuthorizationConsentRepository;
import com.example.authserver.repository.ClientRepository;
import com.example.authserver.services.AuthorizationConsentService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthorizationConsentServiceImpl implements AuthorizationConsentService {

    private final AuthorizationConsentRepository consentRepository;
    private final ClientRepository clientRepository;

    public boolean consentExistsForScope(String clientId,
                                         String scope) {
        return getNewRequestedScopes(clientId,scope).isEmpty();
    }

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

    private Set<String> stringToSet(String delimitedString) {
        String[] stringArray = StringUtils.delimitedListToStringArray(delimitedString," ");
        return new HashSet<>(Set.of(stringArray));
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }

}
