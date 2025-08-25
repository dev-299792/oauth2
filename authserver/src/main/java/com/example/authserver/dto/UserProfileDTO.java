package com.example.authserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UserProfileDTO {

    private ProfileDTO profile = new ProfileDTO();
    private AddressDTO address = new AddressDTO();
    private String phone;

    // Inner class for profile
    @Data
    @NoArgsConstructor
    public static class ProfileDTO {
        private String firstName;
        private String lastName;
        private LocalDate dateOfBirth;
    }

    // Inner class for address
    @Data
    @NoArgsConstructor
    public static class AddressDTO {
        private String street;
        private String city;
        private String state;
        private String postalCode;
        private String country;
    }

}

