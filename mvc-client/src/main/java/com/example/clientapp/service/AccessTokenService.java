package com.example.clientapp.service;

import com.example.clientapp.config.OAuth2Properties;
import com.example.clientapp.dto.AccessTokenRequestDTO;
import com.example.clientapp.dto.AccessTokenResponseDTO;
import com.example.clientapp.util.ObjectUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@AllArgsConstructor
public class AccessTokenService {

    private final RestClient restClient;
    private final OAuth2Properties oAuth2Properties;

    public AccessTokenResponseDTO getAccessToken(AccessTokenRequestDTO requestDTO) {

        String clientId = oAuth2Properties.getClient().getClientId();
        String clientSecret = oAuth2Properties.getClient().getClientSecret();

        AccessTokenResponseDTO responseDTO = null;
        RestClient.ResponseSpec spec =
                restClient
                .post()
                .uri(oAuth2Properties.getServer().getAccessTokenUri())
                        .header(HttpHeaders.AUTHORIZATION,
                                "Basic " + HttpHeaders.encodeBasicAuth(clientId,clientSecret,null))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(ObjectUtil.toMultiValueMap(requestDTO))
                .retrieve();
        responseDTO = spec.body(AccessTokenResponseDTO.class);
        return responseDTO;
    }

}
