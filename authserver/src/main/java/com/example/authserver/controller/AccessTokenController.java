package com.example.authserver.controller;

import com.example.authserver.dto.AccessTokenRequestDTO;
import com.example.authserver.dto.AccessTokenResponseDTO;
import com.example.authserver.services.AccessTokenService;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


/**
 * The AccessTokenController handles the OAuth2 access token endpoint.
 * This class is responsible for issuing new access tokens based on different grant types.
 *
 * @see AccessTokenService
 *
 */
@RestController
@RequestMapping("/api/oauth2")
@AllArgsConstructor
public class AccessTokenController {

    private final AccessTokenService accessTokenService;

    /**
     * Endpoint to issue new access tokens.
     * Receives a request in the form of a url-encoded body with the following fields:
     * - grant_type (required): The type of grant being requested.
     * - code (required for authorization_code grant_type): The authorization code received from the client.
     * - refresh_token (required for refresh_token grant_type): The refresh token received from the client.
     * - redirect_uri (required): The redirect URI used in the authorization request.
     * - client_id (required): The client ID.
     *
     * @param requestDTO the request DTO containing the required fields.
     * @return a ResponseEntity containing the generated AccessTokenResponseDTO,
     *         or an error response if the request is invalid.
     */
    @PostMapping(value = "token",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE )
    ResponseEntity<AccessTokenResponseDTO> generateAccessToken(@ModelAttribute AccessTokenRequestDTO requestDTO) {

        AccessTokenResponseDTO dto = accessTokenService.generateAccessToken(requestDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto);
    }

}
