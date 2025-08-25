package com.example.authserver.repository.user;

import com.example.authserver.entity.user.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile,String> {
}
