package com.example.authserver.controller;

import com.example.authserver.dto.ClientAuthorizationRedirectParams;
import com.example.authserver.services.ClientAuthorizationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@AllArgsConstructor
public class AuthorizationController {

    private final ClientAuthorizationService authorizationService;

    @GetMapping("/oauth2/authorize")
    RedirectView authorize( @ModelAttribute ClientAuthorizationRedirectParams params) {
        String authorizationCode = authorizationService.getAuthorizationCode(params);

        String redirectUri = UriComponentsBuilder
                .fromUriString(params.getRedirect_uri())
                .queryParam("code",authorizationCode)
                .toUriString();

        return new RedirectView(redirectUri);
    }

}
