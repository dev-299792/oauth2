package com.example.authserver.repository;

import com.example.authserver.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link Client} entities.
 *
 * @see Client
 */
public interface ClientRepository extends JpaRepository<Client,String> {

    /**
     * Finds all clients created by a specific user.
     *
     * @param createdBy the user who created the clients
     * @return a list of matching {@link Client} entities
     */
    List<Client> findClientByCreatedBy(String createdBy);

    /**
     * Finds a client by its client ID.
     *
     * @param clientId the client identifier
     * @return an Optional containing the matching {@link Client}, if found
     */
    Optional<Client> findClientByClientId(String clientId);

}
