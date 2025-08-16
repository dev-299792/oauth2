package com.example.authserver.controller;

import com.example.authserver.dto.ClientAuthorizationRedirectParams;
import com.example.authserver.exception.RedirectBackWithErrorException;
import com.example.authserver.services.AuthorizationConsentService;
import com.example.authserver.services.ClientAuthorizationService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * The AuthorizationController is responsible for handling client authorization requests
 * and redirecting the user to the consent screen if consent has not yet been given.
 *
 * @see ClientAuthorizationService
 * @see AuthorizationConsentService
 */
@Controller
@AllArgsConstructor
public class AuthorizationController {

    private final ClientAuthorizationService authorizationService;
    private final AuthorizationConsentService consentService;

    /**
     * Authorizes the client request and redirects user to consent screen if consent not yet given.
     * Validates the client details and generates an authorization code. The authorization code is
     * included in the redirect URI unless an error occurred.
     *
     * @param params The client authorization parameters.
     * @param session The HTTP session.
     * @return A redirect view to the redirect URI with the authorization code or an error parameter.
     */
    @GetMapping("/oauth2/authorize")
    RedirectView authorize(@Validated @ModelAttribute ClientAuthorizationRedirectParams params,
                           HttpSession session) {

        // validating request before asking for user consent
        authorizationService.validateClientDetails(params.getClient_id(),params);

        if(!consentService.consentExistsForScope(params.getClient_id(), params.getScope())) {
            session.setAttribute("clientAuthorizationRequest",params);
            return new RedirectView("/oauth2/consent");
        }

        UriComponentsBuilder redirectUri = UriComponentsBuilder
                .fromUriString(params.getRedirect_uri())
                .queryParam("state",params.getState());

        try {
            String authorizationCode = authorizationService.getAuthorizationCode(params);
            redirectUri.queryParam("code",authorizationCode);
        } catch (RedirectBackWithErrorException ex) {
            redirectUri.queryParam("error",ex.getMessage());
        }

        return new RedirectView(redirectUri.toUriString());
    }

}
