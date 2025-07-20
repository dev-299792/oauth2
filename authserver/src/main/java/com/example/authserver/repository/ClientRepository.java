package com.example.authserver.repository;

import com.example.authserver.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client,String> {

    List<Client> findClientByCreatedBy(String createdBy);

    Optional<Client> findClientByClientId(String clientId);

}
