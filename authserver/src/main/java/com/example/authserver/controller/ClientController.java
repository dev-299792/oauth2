package com.example.authserver.controller;

import com.example.authserver.dto.ClientRegRequestDTO;
import com.example.authserver.dto.ClientRegResponseDTO;
import com.example.authserver.enums.ApplicationType;
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


/**
 * ClientController is the controller for handling client registration and client list views.
 *
 * @see ClientRegistrationService
 */
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


    /**
     * Handles the submission of the client registration form.
     *
     * @param client          The client registration request params.
     * @param bindingResult   The binding result of the form submission.
     * @param model           The model map for the view.
     * @return                The name of the view.
     */
    @PostMapping("/add-client")
    String registerClient(@Valid @ModelAttribute("client") ClientRegRequestDTO client,
                          BindingResult bindingResult,
                          Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("applicationTypes", ApplicationType.values());
            return "add-client";
        }

        ClientRegResponseDTO responseDTO = clientRegistrationService.registerClient(client);
        model.addAttribute("generatedClient", clientRegistrationService.convertClientRegResponseToMap(responseDTO));
        return "add-client";

    }

    /**
     * Handles the GET request to list all clients of the current user.
     *
     * @param model The model map for the view.
     * @return The name of the view.
     */
    @GetMapping("/clients")
    String listClient(Model model) {

        List<Map<String,String>> clients = clientRegistrationService
                .getAllClientsOfCurrentUser()
                .stream()
                .map(clientRegistrationService::convertClientRegResponseToMap)
                .toList();

        model.addAttribute("clientList",clients);

        return "clients";
    }

}
