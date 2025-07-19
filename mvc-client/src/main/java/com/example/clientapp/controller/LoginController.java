package com.example.clientapp.controller;

import com.example.clientapp.config.OAuth2Properties;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@AllArgsConstructor
public class LoginController {

    OAuth2Properties properties;

    @GetMapping("/")
    String home() {
        return "home";
    }

    @GetMapping("/oauth2/login")
    RedirectView oauth2Login() {

        String authServerUrl = properties.getServer().getAuthorizationUri();
        String clientId = properties.getClient().getClientId();
        String redirectUri = properties.getClient().getRedirectUri();
        String scope = "profile";

        String redirectUrl = UriComponentsBuilder.fromUriString(authServerUrl)
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", scope)
                .toUriString();

        return new RedirectView(redirectUrl);
    }

}
