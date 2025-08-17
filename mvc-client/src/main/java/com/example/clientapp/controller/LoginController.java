package com.example.clientapp.controller;

import com.example.clientapp.config.OAuth2Properties;
import com.example.clientapp.util.SecurityUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@AllArgsConstructor
public class LoginController {

    OAuth2Properties properties;

    @GetMapping("/")
    String home(Model model, HttpSession session) {
        String code = (String) session.getAttribute("code");
        if(code != null ) {
            model.addAttribute("code",code);
        }
        return "home";
    }

    @GetMapping("/oauth2/login")
    RedirectView oauth2Login(HttpServletResponse response) {

        String authServerUrl = properties.getServer().getAuthorizationUri();
        String clientId = properties.getClient().getClientId();
        String redirectUri = properties.getClient().getRedirectUri();
        String scope = properties.getClient().getScopes();

        String state = SecurityUtil.generateRandomState();
        Cookie cookie = new Cookie("state",state);
        response.addCookie(cookie);

        String redirectUrl = UriComponentsBuilder.fromUriString(authServerUrl)
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", scope)
                .queryParam("state",state)
                .toUriString();

        return new RedirectView(redirectUrl);
    }

}
