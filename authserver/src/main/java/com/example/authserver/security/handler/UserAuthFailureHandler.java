package com.example.authserver.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom authentication failure handler for user login.
 * <p>
 * Extends {@link SimpleUrlAuthenticationFailureHandler} to provide
 * custom redirection behavior when authentication fails.
 * </p>
 * <ul>
 *     <li>If the user's account is disabled (e.g., email not verified),
 *     redirects to {@code /login?error=verify}.</li>
 *     <li>For all other authentication failures, redirects to {@code /login?error}.</li>
 * </ul>
 */
@Component
public class UserAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        if (exception instanceof DisabledException) {
            // account disabled → show verification pending
            getRedirectStrategy().sendRedirect(request, response, "/login?error=verify");
        } else {
            getRedirectStrategy().sendRedirect(request, response, "/login?error");
        }
    }
}
