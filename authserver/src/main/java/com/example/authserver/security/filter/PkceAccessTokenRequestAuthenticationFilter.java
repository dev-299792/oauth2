package com.example.authserver.security.filter;

import com.example.authserver.security.authentication.PkceAutheticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class PkceAccessTokenRequestAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        if ("/api/oauth2/token".equals(request.getRequestURI()) &&
                request.getParameterMap().containsKey("code_verifier") ) {

            String authorizationCode = request.getParameter("code");
            String codeVerifier = request.getParameter("code_verifier");

            PkceAutheticationToken unauthenticatedPkceToken = PkceAutheticationToken
                                            .unauthenticated(authorizationCode,codeVerifier);

            try {

                Authentication authenticated = authenticationManager.authenticate(unauthenticatedPkceToken);

                if (authenticated.isAuthenticated()) {
                    SecurityContextHolder.getContext().setAuthentication(authenticated);
                    filterChain.doFilter(request, response);
                }
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}