package com.example.authserver.dto;

import com.example.authserver.entity.user.Address;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder
public class UserInfoResponseDTO {

    // Core identifier ( required for OIDC)
    private String sub;

    // Profile claims
    private String name;
    private String givenName;
    private String familyName;
    private String middleName;
    private String nickname;
    private String preferredUsername;
    private String birthdate;
    private Instant updatedAt;

    // Email claims
    private String email;
    private Boolean emailVerified;

    // Address claims
    private Map<String,String> address;

    // Phone claims
    private String phoneNumber;
    private Boolean phoneNumberVerified;

}