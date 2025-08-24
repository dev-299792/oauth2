package com.example.authserver.controller;

import com.example.authserver.services.VerificationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller responsible for handling email verification requests.
 * <p>
 * Provides an endpoint to verify a user's email based on a verification token.
 * Depending on the validity of the token, it either redirects the user to the login page
 * or shows an error page.
 * </p>
 *
 * @see VerificationTokenService
 */
@Controller
@AllArgsConstructor
public class VerificationController {

    private final VerificationTokenService verificationTokenService;


    /**
     * Handles HTTP GET requests for email verification.
     * <p>
     * Validates the token provided as a request parameter. If the token is valid,
     * the user is redirected to the login page with a "verified" query parameter.
     * If the token is invalid or expired, an error message is added to the model
     * and the error view is returned.
     * </p>
     *
     * @param token the verification token received in the request
     * @param model the {@link Model} used to pass attributes to the view
     * @return a redirect URL to the login page if verification is successful,
     *         or the name of the error view if the token is invalid or expired
     */
    @GetMapping("/verify")
    String verifyEmail(@RequestParam("token")String token, Model model) {

        if(verificationTokenService.verifyToken(token)) {
            return "redirect:/login?verified";
        }
        model.addAttribute("errorMessage",
                "Verification link is invalid or expired.");
        return "error";
    }

}
