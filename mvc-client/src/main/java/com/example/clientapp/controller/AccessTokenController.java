package com.example.clientapp.controller;

import com.example.clientapp.config.OAuth2Properties;
import com.example.clientapp.dto.AccessTokenRequestDTO;
import com.example.clientapp.dto.AccessTokenResponseDTO;
import com.example.clientapp.service.AccessTokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class AccessTokenController {

    private final OAuth2Properties oAuth2Properties;
    private final AccessTokenService tokenService;
    private final ObjectMapper mapper;

    @GetMapping("/oauth2/callback")
    String oauthCallback(@RequestParam(required = false) String code,
                         @RequestParam(required = false) String error,
                         @RequestParam String state,
                         @CookieValue("state") String stateCookie,
                         HttpSession session,
                         Model model) throws JsonProcessingException
    {
        if(stateCookie == null || !stateCookie.equals(state)) {
            throw new RuntimeException("Error in validation.");
        }

        if(error != null) {
            throw new RuntimeException(error);
        }

        AccessTokenRequestDTO dto = AccessTokenRequestDTO
                .builder()
                .code(code)
                .grant_type("authorization_code")
                .redirect_uri(oAuth2Properties.getClient().getRedirectUri())
                .build();

        AccessTokenResponseDTO responseDTO = tokenService.getAccessToken(dto);

        session.setAttribute("code",mapper.writeValueAsString(responseDTO));
        return "redirect:/";
    }

    @GetMapping("/clear-token")
    String clearToken(HttpSession session) {
        if(session.getAttribute("code")!=null) {
            session.removeAttribute("code");
        }
        return "redirect:/";
    }

}
