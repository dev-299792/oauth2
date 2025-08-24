package com.example.authserver.security.config;

import com.example.authserver.security.authentication.ClientAuthenticationProvider;
import com.example.authserver.security.authentication.PkceAuthenticationProvider;
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

    private final ClientAuthenticationProvider clientAuthenticationProvider;
    private final PkceAuthenticationProvider pkceAuthenticationProvider;
    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final PkceAccessTokenRequestAuthenticationFilter pkceAuthenticationFilter;
    private final UserAuthFailureHandler userAuthFailureHandler;

    /** Security for client API endpoints (/api/**). */
    @Bean
    @Order(1)
    public SecurityFilterChain clientSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
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

    /** Security for user-facing endpoints (web pages, login, register). */
    @Bean
    @Order(2)
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
        http
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
