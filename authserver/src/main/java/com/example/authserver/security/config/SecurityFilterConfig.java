package com.example.authserver.security.config;

import com.example.authserver.security.authentication.BearerTokenAuthenticationProvider;
import com.example.authserver.security.authentication.ClientAuthenticationProvider;
import com.example.authserver.security.authentication.PkceAuthenticationProvider;
import com.example.authserver.security.filter.BearerTokenAuthenticationFilter;
import com.example.authserver.security.filter.PkceAccessTokenRequestAuthenticationFilter;
import com.example.authserver.security.handler.UserAuthFailureHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Configures Spring Security for the Authorization Server.
 *
 * <p>Defines two independent filter chains:</p>
 * <ul>
 *   <li><b>Client API security (Order 1)</b>: Secures <code>/api/**</code> endpoints using
 *       {@link ClientAuthenticationProvider} and {@link PkceAuthenticationProvider}, plus
 *       {@link PkceAccessTokenRequestAuthenticationFilter} for PKCE validation.</li>
 *   <li><b>User web security (Order 2)</b>: Secures UI endpoints with
 *       {@link DaoAuthenticationProvider}, form login, and permits access to <code>/login</code>
 *       and <code>/register</code>.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityFilterConfig {

    private static final RequestMatcher ACCESS_TOKEN_REQUEST_MATCHER =
            PathPatternRequestMatcher.withDefaults().matcher("/api/oauth2/token");

    private static final RequestMatcher EXCEPT_ACCESS_TOKEN_REQUEST_MATCHER = new AndRequestMatcher(
            new OrRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher("/api/**"),
                                PathPatternRequestMatcher.withDefaults().matcher("/userinfo")),
            new NegatedRequestMatcher(ACCESS_TOKEN_REQUEST_MATCHER)
    );

    private static final RequestMatcher NON_REST_API_REQUEST_MATCHER =
            new NegatedRequestMatcher(
                    new AndRequestMatcher(ACCESS_TOKEN_REQUEST_MATCHER,
                    EXCEPT_ACCESS_TOKEN_REQUEST_MATCHER)
            );

    private final ClientAuthenticationProvider clientAuthenticationProvider;
    private final PkceAuthenticationProvider pkceAuthenticationProvider;
    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final PkceAccessTokenRequestAuthenticationFilter pkceAuthenticationFilter;
    private final BearerTokenAuthenticationFilter bearerTokenAuthenticationFilter;
    private final BearerTokenAuthenticationProvider bearerTokenAuthenticationProvider;
    private final UserAuthFailureHandler userAuthFailureHandler;

    /** Security for access token endpoint (/api/oauth2/token) */
    @Bean
    @Order(1)
    public SecurityFilterChain accessTokenRequestSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(ACCESS_TOKEN_REQUEST_MATCHER)
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(pkceAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(pkceAuthenticationProvider)
                .authenticationProvider(clientAuthenticationProvider)
                .authorizeHttpRequests( authorize ->
                        authorize.anyRequest().authenticated())
                .httpBasic(httpBasic -> httpBasic
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

        return http.build();
    }

    /** Security for client API endpoints (/api/**,/userinfo). except access token endpoint */
    @Bean
    @Order(2)
    public SecurityFilterChain restSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(EXCEPT_ACCESS_TOKEN_REQUEST_MATCHER)
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(bearerTokenAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(bearerTokenAuthenticationProvider)
                .authorizeHttpRequests( authorize ->
                        authorize.anyRequest().authenticated())
                .httpBasic(httpBasic -> httpBasic
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

        return http.build();
    }

    /** Security for user-facing endpoints (web pages, login, register). */
    @Bean
    @Order(3)
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {

        http
                .securityMatcher(NON_REST_API_REQUEST_MATCHER)
                .authenticationProvider(daoAuthenticationProvider)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/login","/register","/verify-email","/verify").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureHandler(userAuthFailureHandler)
                        .permitAll()
                );

        return http.build();
    }

}
