package com.example.authserver.repository;

import com.example.authserver.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client,String> {

    List<Client> findClientByCreatedBy(String createdBy);

}
