package com.example.authserver.controller;

import com.example.authserver.dto.ClientAuthorizationRedirectParams;
import com.example.authserver.exception.RedirectBackWithErrorException;
import com.example.authserver.services.ClientAuthorizationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@AllArgsConstructor
public class AuthorizationController {

    private final ClientAuthorizationService authorizationService;

    @GetMapping("/oauth2/authorize")
    RedirectView authorize( @Validated @ModelAttribute ClientAuthorizationRedirectParams params) {

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
