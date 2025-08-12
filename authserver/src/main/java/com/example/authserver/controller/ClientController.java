package com.example.authserver.controller;

import com.example.authserver.dto.ClientRegRequestDTO;
import com.example.authserver.dto.ClientRegResponseDTO;
import com.example.authserver.enums.ApplicationType;
import com.example.authserver.enums.GrantType;
import com.example.authserver.helper.PojoHelper;
import com.example.authserver.services.ClientRegistrationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class ClientController {

    private final ClientRegistrationService clientRegistrationService;

    @GetMapping("/add-client")
    String clientRegistration(Model model) {
        model.addAttribute("client",new ClientRegRequestDTO());
        model.addAttribute("applicationTypes", ApplicationType.values());
        return "add-client";
    }

    @PostMapping("/add-client")
    String registerClient(@Valid @ModelAttribute("client") ClientRegRequestDTO client,
                          BindingResult bindingResult,
                          Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("applicationTypes", ApplicationType.values());
            return "add-client";
        }

        ClientRegResponseDTO responseDTO = clientRegistrationService.registerClient(client);
        model.addAttribute("generatedClient", PojoHelper.convertToMap(responseDTO));
        return "add-client";

    }

    @GetMapping("/clients")
    String listClient(Model model) {

        List<Map<String,String>> clients = clientRegistrationService
                .getAllClientsOfCurrentUser()
                .stream()
                .map(ClientRegResponseDTO::new)
                .map(PojoHelper::convertToMap)
                .toList();

        model.addAttribute("clientList",clients);

        return "clients";
    }

}
