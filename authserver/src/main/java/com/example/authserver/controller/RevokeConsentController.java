package com.example.authserver.controller;

import com.example.authserver.dto.ConsentDTO;
import com.example.authserver.services.AuthorizationConsentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class RevokeConsentController {

    private final AuthorizationConsentService consentService;

    @GetMapping("/revoke/consents")
    String showRevokeConsentPage(Model model) {
        List<ConsentDTO> clientConsents = consentService.getClientConsents();
        model.addAttribute("clientConsents", clientConsents);
        return "revoke-consents";
    }

    @PostMapping("/revoke/consents")
    String revokeConsents(@RequestParam String clientId) {

        consentService.revokeConsents(clientId);

        return "redirect:/revoke/consents";
    }

}
