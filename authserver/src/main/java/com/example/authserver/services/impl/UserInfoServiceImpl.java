package com.example.authserver.services.impl;

import com.example.authserver.dto.UserInfoDTO;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private final AddressRepository addressRepository;
    private final PhoneRepository phoneRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    @Override
    public void saveUserData(UserInfoDTO userInfoDTO) {

        User user = getAuthenticatedUser();

        UserProfile profile = new UserProfile();
        profile.setId(UUID.randomUUID().toString());
        profile.setFirstName(userInfoDTO.getProfile().getFirstName());
        profile.setLastName(userInfoDTO.getProfile().getLastName());
        profile.setDateOfBirth(userInfoDTO.getProfile().getDateOfBirth());
        profile.setUser(user);
        userProfileRepository.save(profile);

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setStreet(userInfoDTO.getAddress().getStreet());
        address.setCity(userInfoDTO.getAddress().getCity());
        address.setState(userInfoDTO.getAddress().getState());
        address.setPostalCode(userInfoDTO.getAddress().getPostalCode());
        address.setCountry(userInfoDTO.getAddress().getCountry());
        address.setUser(user);
        addressRepository.save(address);

        Phone phone = new Phone();
        phone.setId(UUID.randomUUID().toString());
        phone.setNumber(userInfoDTO.getPhone());
        phone.setUser(user);
        phoneRepository.save(phone);

    }

    public Map<String,String> getUserProfile() {

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

    /**
     * Retrieves the currently authenticated user from the security context.
     *
     * @return the authenticated User entity
     */
    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }
}
