package com.example.authserver.controller;

import com.example.authserver.dto.ClientAuthorizationRedirectParams;
import com.example.authserver.entity.Client;
import com.example.authserver.exception.InvalidRequestException;
import com.example.authserver.exception.RedirectBackWithErrorException;
import com.example.authserver.services.AuthorizationConsentService;
import com.example.authserver.services.ClientAuthorizationService;
import com.example.authserver.services.ClientRegistrationService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Set;

@Controller
@AllArgsConstructor
public class ConsentController {

    private final AuthorizationConsentService consentService;
    private final ClientAuthorizationService authorizationService;
    private final ClientRegistrationService clientService;

    /**
     * Displays the consent screen to the user. The consent screen is shown when the user has not
     * previously given consent for the client to access their data, or when the client is requesting
     * additional scopes that the user has previously approved.
     *
     * @param session HTTP session object
     * @param model   Model object for passing data to the view
     * @return The name of the consent view to render
     * @throws InvalidRequestException if there is an issue with the request or the client
     */
    @GetMapping("/oauth2/consent")
    public String consent(HttpSession session, Model model) {
        ClientAuthorizationRedirectParams params = (ClientAuthorizationRedirectParams) session.getAttribute("clientAuthorizationRequest");

        if (params == null) {
            throw new InvalidRequestException("Invalid request.");
        }
        Set<String> newScopes = consentService.getNewRequestedScopes(params.getClient_id(), params.getScope());
        Client client = clientService.getClient(params.getClient_id())
                .orElseThrow(()->new InvalidRequestException("Invalid request."));

        model.addAttribute("clientName", client.getClientName());
        model.addAttribute("newScopes", newScopes);
        model.addAttribute("clientId", params.getClient_id());

        return "consent";
    }

    /**
     * Processes the user's consent to the client's requested scopes. If the user has approved the
     * requested scopes, an authorization code is generated and included in the redirect URI. If the
     * user has denied the request, an error parameter is included in the redirect URI.
     *
     * @param approvedScopes The scopes that the user has approved. If empty or null, the user has
     *                       denied the request.
     * @param clientId       The client ID that the user has given consent to.
     * @param session        The HTTP session object.
     * @return A redirect view to the redirect URI with the authorization code or an error parameter.
     * @throws InvalidRequestException if there is an issue with the request or the client
     */
    @PostMapping("/oauth2/consent")
    public RedirectView processConsent(@RequestParam(name = "scopes", required = false) Set<String> approvedScopes,
                                       @RequestParam("clientId") String clientId,
                                       HttpSession session) {

        ClientAuthorizationRedirectParams params = (ClientAuthorizationRedirectParams) session.getAttribute("clientAuthorizationRequest");

        if (params == null || !params.getClient_id().equals(clientId)) {
            throw new InvalidRequestException("Invalid request.");
        }

        UriComponentsBuilder redirectUri = UriComponentsBuilder
                .fromUriString(params.getRedirect_uri())
                .queryParam("state", params.getState());

        if (approvedScopes == null || approvedScopes.isEmpty()) {
            session.removeAttribute("clientAuthorizationRequest");
            redirectUri.queryParam("error", "access_denied");
            return new RedirectView(redirectUri.toUriString());
        }

        params.setScope(String.join(" ", approvedScopes));

        try {
            consentService.saveConsent(params.getClient_id(), String.join(" ", approvedScopes));
            String authorizationCode = authorizationService.getAuthorizationCode(params);
            redirectUri.queryParam("code", authorizationCode);
        } catch (RedirectBackWithErrorException ex) {
            redirectUri.queryParam("error", ex.getMessage());
        } finally {
            session.removeAttribute("clientAuthorizationRequest");
        }

        return new RedirectView(redirectUri.toUriString());
    }
}