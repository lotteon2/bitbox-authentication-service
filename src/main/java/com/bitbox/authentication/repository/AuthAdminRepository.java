package com.bitbox.authentication.repository;

import com.bitbox.authentication.entity.AuthAdmin;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AuthAdminRepository extends CrudRepository<AuthAdmin, String> {
    Optional<AuthAdmin> findByAdminEmailAndDeletedIsFalse(String adminEmail);
}
