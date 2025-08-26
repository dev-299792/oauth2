package com.example.authserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class ConsentDTO {
    private String clientId;
    private Set<String> scopes;
}
