package com.example.authserver.services.impl;

import com.example.authserver.dto.UserInfoResponseDTO;
import com.example.authserver.dto.UserProfileDTO;
import com.example.authserver.entity.User;
import com.example.authserver.entity.user.Address;
import com.example.authserver.entity.user.Phone;
import com.example.authserver.entity.user.UserProfile;
import com.example.authserver.exception.ProfileNotUpdatedException;
import com.example.authserver.repository.UserRepository;
import com.example.authserver.repository.user.AddressRepository;
import com.example.authserver.repository.user.PhoneRepository;
import com.example.authserver.repository.user.UserProfileRepository;
import com.example.authserver.services.UserInfoService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private final AddressRepository addressRepository;
    private final PhoneRepository phoneRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    @Override
    public void saveUserData(UserProfileDTO userProfileDTO) {

        User user = getAuthenticatedUser();

        UserProfile profile = new UserProfile();
        profile.setId(UUID.randomUUID().toString());
        profile.setFirstName(userProfileDTO.getProfile().getFirstName());
        profile.setLastName(userProfileDTO.getProfile().getLastName());
        profile.setDateOfBirth(userProfileDTO.getProfile().getDateOfBirth());
        profile.setUser(user);
        userProfileRepository.save(profile);

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setStreet(userProfileDTO.getAddress().getStreet());
        address.setCity(userProfileDTO.getAddress().getCity());
        address.setState(userProfileDTO.getAddress().getState());
        address.setPostalCode(userProfileDTO.getAddress().getPostalCode());
        address.setCountry(userProfileDTO.getAddress().getCountry());
        address.setUser(user);
        addressRepository.save(address);

        Phone phone = new Phone();
        phone.setId(UUID.randomUUID().toString());
        phone.setNumber(userProfileDTO.getPhone());
        phone.setUser(user);
        phoneRepository.save(phone);

    }

    public Map<String,String> getCompleteUserProfile() {

        User user = userRepository.findByUsername(getAuthenticatedUser().getUsername()).orElse(null);

        UserProfile profile = user.getProfile();
        Address address = user.getAddresses().stream().findFirst().orElse(null);
        Phone phone = user.getPhones().stream().findFirst().orElse(null);

        if(profile == null || address == null || phone == null ) {
            throw new ProfileNotUpdatedException();
        }

        // Prepare data for success page
        Map<String, String> savedUser = new LinkedHashMap<>();
        savedUser.put("First Name", profile.getFirstName());
        savedUser.put("Last Name", profile.getLastName());
        savedUser.put("Date of Birth", profile.getDateOfBirth() != null ? profile.getDateOfBirth().toString() : "");
        savedUser.put("Street", address.getStreet());
        savedUser.put("City", address.getCity());
        savedUser.put("State", address.getState());
        savedUser.put("Postal Code", address.getPostalCode());
        savedUser.put("Country", address.getCountry());
        savedUser.put("Phone", phone.getNumber());

        return savedUser;
    }

    @Override
    public UserInfoResponseDTO getScopeBasedUserInfo() {

        User user = getAuthenticatedUser();

        var authorities = getAuthorities();

        Set<String> scopes = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(s -> s.startsWith("SCOPE_"))
                .map(s -> s.substring("SCOPE_".length()))
                .collect(Collectors.toSet());

        if(!scopes.contains("openid")) {
            return null;
        }

        UserInfoResponseDTO.UserInfoResponseDTOBuilder builder =
                UserInfoResponseDTO.builder();

        builder.sub(user.getUser_id());

        if(scopes.contains("email")) {
            builder.email(user.getEmail());
            builder.emailVerified(user.isEnabled());
        }

        if(scopes.contains("phone")) {
            Phone phone = user.getPhones().stream().findFirst().orElse(null);
            if(phone!=null) {
                builder.phoneNumber(phone.getNumber());
                builder.phoneNumberVerified(false);
            }
        }

        if(scopes.contains("profile")) {
            UserProfile profile = user.getProfile();
            if(profile!=null) {
                builder.name(profile.getFirstName() + " " + profile.getLastName());
                builder.givenName(profile.getFirstName());
                builder.familyName(profile.getLastName());
                builder.birthdate(profile.getDateOfBirth().toString());
            }
        }

        if(scopes.contains("address")) {
            Address address = user.getAddresses().stream().findFirst().orElse(null);
            if(address!=null) {
                Map<String,String> map = new LinkedHashMap<>();
                map.put("street", address.getStreet());
                map.put("city",address.getCity());
                map.put("state", address.getState());
                map.put("postalCode", address.getPostalCode());
                map.put("country", address.getCountry());
                builder.address(map);
            }
        }

        return builder.build();
    }

    /**
     * Retrieves the currently authenticated user from the security context.
     *
     * @return the authenticated User entity
     */
    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }

    private Collection<? extends GrantedAuthority> getAuthorities() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities();
    }

}
