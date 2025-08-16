package com.example.authserver.services;

import com.example.authserver.dto.ClientRegRequestDTO;
import com.example.authserver.dto.ClientRegResponseDTO;
import com.example.authserver.entity.Client;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ClientRegistrationService {
    Optional<Client> getClient(String clientId);
    List<ClientRegResponseDTO> getAllClientsOfCurrentUser();
    ClientRegResponseDTO registerClient(ClientRegRequestDTO clientRegRequest);
    Map<String, String> convertClientRegResponseToMap(ClientRegResponseDTO dto);
}