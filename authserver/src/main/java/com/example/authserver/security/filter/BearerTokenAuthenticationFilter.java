package com.example.authserver.security.filter;

import com.example.authserver.security.authentication.BearerTokenAuthenticationToken;
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

/**
 * Filter that intercepts REST API requests and validates Bearer tokens.
 *
 * <p>
 * This filter triggers for requests under {@code /api/**} that include an
 * {@code Authorization: Bearer <token>} header.
 * It builds a {@link BearerTokenAuthenticationToken} (unauthenticated)
 * and delegates authentication to the {@link AuthenticationManager}.
 * </p>
 *
 * <p>
 * On successful validation, the filter sets the authenticated token in the
 * {@link SecurityContextHolder} and continues the filter chain.
 * On failure, it responds with {@code 401 Unauthorized}.
 * </p>
 *
 * @see BearerTokenAuthenticationToken
 * @see com.example.authserver.security.authentication.BearerTokenAuthenticationProvider
 */
@Component
@AllArgsConstructor
public class BearerTokenAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException, ServletException {

        String requestUri = request.getRequestURI();
        String authorizationHeader = request.getHeader("Authorization");

        // Apply filter only for /api/** and Authorization: Bearer tokens
        if ( (requestUri.startsWith("/api/") || requestUri.equals("/userinfo") )
                && authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            BearerTokenAuthenticationToken unauthenticatedToken =
                    BearerTokenAuthenticationToken.unauthenticated(token);

            try {
                Authentication authenticated = authenticationManager.authenticate(unauthenticatedToken);

                if (authenticated.isAuthenticated()) {
                    SecurityContextHolder.getContext().setAuthentication(authenticated);
                    filterChain.doFilter(request, response);
                    return;
                }
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }
        }

        // Continue chain if not applicable
        filterChain.doFilter(request, response);
    }
}
