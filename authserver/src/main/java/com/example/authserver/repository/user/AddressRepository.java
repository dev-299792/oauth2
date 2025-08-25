package com.example.authserver.repository.user;

import com.example.authserver.entity.user.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address,String> {
}
