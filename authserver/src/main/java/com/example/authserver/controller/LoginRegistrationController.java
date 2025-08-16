package com.example.authserver.controller;

import com.example.authserver.dto.UserDTO;
import com.example.authserver.exception.UserAlreadyExistsException;
import com.example.authserver.services.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
public class LoginRegistrationController {

    private final RegistrationService registrationService;

    @GetMapping("/login")
    String login() {
        return "login";
    }

    @GetMapping("/")
    String home() {
        return "home";
    }

    @GetMapping("/register")
    String register(Model model) {
        model.addAttribute("user",new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    String registerSubmit(@ModelAttribute("user") UserDTO userDTO, Model model) {

        if(userDTO.getUsername()==null || userDTO.getPassword()==null ||
                userDTO.getUsername().isBlank() || userDTO.getPassword().isBlank() ) {
            model.addAttribute("error"
                    ,"Username and Password are required");
            return "register";
        }

        try {
            registrationService.registerUser(userDTO);
            return "redirect:/login?registerSuccess";
        } catch (UserAlreadyExistsException e) {
            model.addAttribute("error"
                    ,"Username already exists!");
            return "register";
        }
    }

}
