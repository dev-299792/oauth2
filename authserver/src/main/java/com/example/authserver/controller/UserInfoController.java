package com.example.authserver.controller;

import com.example.authserver.dto.UserInfoResponseDTO;
import com.example.authserver.services.UserInfoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;

    @GetMapping("/userinfo")
    ResponseEntity<UserInfoResponseDTO> getUserInfo() {

        UserInfoResponseDTO dto = userInfoService.getScopeBasedUserInfo();

        return ResponseEntity.ok(dto);
    }

}
