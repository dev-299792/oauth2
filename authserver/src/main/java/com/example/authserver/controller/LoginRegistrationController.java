package com.example.authserver.controller;

import com.example.authserver.dto.UserDTO;
import com.example.authserver.exception.EmailAlreadyUsedException;
import com.example.authserver.exception.UserAlreadyExistsException;
import com.example.authserver.services.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * The LoginRegistrationController is responsible for handling login and registration requests.
 *
 * @see RegistrationService
 *
 */
@Controller
@AllArgsConstructor
public class LoginRegistrationController {

    private final RegistrationService registrationService;

    /**
     * Handles the HTTP GET request to the login page.
     *
     * @return The name of the view to render.
     */
    @GetMapping("/login")
    String login() {
        return "login";
    }

    /**
     * Handles the HTTP GET request to the home page.
     *
     * @return The name of the view to render.
     */
    @GetMapping("/")
    String home() {
        return "home";
    }

    /**
     * Handles the HTTP GET request to the registration page.
     * It initializes a new UserDTO object and adds it to the model.
     *
     * @param model The model object for passing data to the view.
     * @return The name of the view to render.
     */
    @GetMapping("/register")
    String register(Model model) {
        model.addAttribute("user",new UserDTO());
        return "register";
    }

    /**
     * Handles the HTTP POST request to the registration page.
     * It validates the userDTO object and either redirects to the login page with a success message
     * or renders the registration page with an error message if the userDTO object is invalid.
     *
     * @param userDTO The user registration information.
     * @param model The model object for passing data to the view.
     * @return The name of the view to render.
     */
    @PostMapping("/register")
    String registerSubmit(@Validated @ModelAttribute("user") UserDTO userDTO,
                            BindingResult bindingResult,
                            Model model) {
        if(bindingResult.hasErrors()) {
            return "register";
        }
        try {
            registrationService.registerUser(userDTO);
            return "redirect:/verify-email";
        } catch (UserAlreadyExistsException e) {
            bindingResult.rejectValue("username",null,"Username already exists!");
            return "register";
        } catch (EmailAlreadyUsedException e) {
            bindingResult.rejectValue("email",null,"Email already used!");
            return "register";
        }
    }

/**
 * Handles HTTP GET requests for the email verification page.
 * <p>
 * This method renders the "verify-email" view, which contains
 * message instructing the user to check their email and verify their account.
 * </p>
 */
 @GetMapping("/verify-email")
    public String verifyEmailPage() {
        return "verify-email";
    }
}
