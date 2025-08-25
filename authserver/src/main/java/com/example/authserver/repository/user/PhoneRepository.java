package com.example.authserver.repository.user;

import com.example.authserver.entity.user.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneRepository extends JpaRepository<Phone,String> {
}
