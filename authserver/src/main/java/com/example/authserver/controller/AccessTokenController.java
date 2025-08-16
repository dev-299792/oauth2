package com.example.authserver.controller;

import com.example.authserver.dto.AccessTokenRequestDTO;
import com.example.authserver.dto.AccessTokenResponseDTO;
import com.example.authserver.entity.AccessToken;
import com.example.authserver.services.AccessTokenService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oauth2")
@AllArgsConstructor
public class AccessTokenController {

    private final AccessTokenService accessTokenService;

    @PostMapping(value = "token",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE )
    ResponseEntity<AccessTokenResponseDTO> generateAccessToken(@ModelAttribute AccessTokenRequestDTO requestDTO) {

        AccessTokenResponseDTO dto = accessTokenService.generateAccessToken(requestDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto);
    }

}
