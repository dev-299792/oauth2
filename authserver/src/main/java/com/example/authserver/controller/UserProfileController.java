package com.example.authserver.controller;

import com.example.authserver.dto.UserProfileDTO;
import com.example.authserver.exception.ProfileNotUpdatedException;
import com.example.authserver.services.UserInfoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
@AllArgsConstructor
public class UserProfileController {

    private final UserInfoService userInfoService;

    @GetMapping("/profile")
    public String showProfileForm(Model model) {

        try {
            Map<String,String> userProfile = userInfoService.getCompleteUserProfile();
            model.addAttribute("userProfile", userProfile);
        } catch (ProfileNotUpdatedException e) {
            model.addAttribute("userProfileForm", new UserProfileDTO());
        }

        return "profile";
    }

    @PostMapping("/profile")
    public String saveProfile(@ModelAttribute UserProfileDTO userProfileForm, Model model) {

        userInfoService.saveUserData(userProfileForm);
        model.addAttribute("userProfile", userInfoService.getCompleteUserProfile());
        model.addAttribute("savedUser",true);
        return "profile";
    }
}

