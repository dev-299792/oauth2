package com.example.authserver.services;

import com.example.authserver.dto.UserInfoDTO;

import java.util.Map;

public interface UserInfoService {
    void saveUserData(UserInfoDTO profile);
    Map<String,String> getUserProfile();
}
