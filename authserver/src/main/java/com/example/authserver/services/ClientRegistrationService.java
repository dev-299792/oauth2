package com.example.authserver.services;

import com.example.authserver.dto.ClientRegRequestDTO;
import com.example.authserver.dto.ClientRegResponseDTO;
import com.example.authserver.entity.Client;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for managing OAuth2 client registrations.
 */
public interface ClientRegistrationService {

    /**
     * Retrieves a client by its client ID.
     *
     * @param clientId the client ID
     * @return an Optional containing the client if found
     */
    Optional<Client> getClient(String clientId);

    /**
     * Retrieves all clients created by the currently authenticated user.
     *
     * @return a list of client registration response DTOs
     */
    List<ClientRegResponseDTO> getAllClientsOfCurrentUser();

    /**
     * Registers a new client.
     *
     * @param clientRegRequest the client registration request DTO
     * @return the registered client as a response DTO
     */
    ClientRegResponseDTO registerClient(ClientRegRequestDTO clientRegRequest);

    /**
     * Converts a client registration response DTO to a map of key-value pairs.
     *
     * @param dto the client registration response DTO
     * @return a map representation of the DTO
     */
    Map<String, String> convertClientRegResponseToMap(ClientRegResponseDTO dto);
}
