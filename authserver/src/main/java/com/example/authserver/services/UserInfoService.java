package com.example.authserver.services;

import com.example.authserver.dto.UserInfoResponseDTO;
import com.example.authserver.dto.UserProfileDTO;

import java.util.Map;

public interface UserInfoService {
    void saveUserData(UserProfileDTO profile);
    Map<String,String> getCompleteUserProfile();
    UserInfoResponseDTO getScopeBasedUserInfo();
}
