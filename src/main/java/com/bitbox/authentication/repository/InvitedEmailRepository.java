package com.bitbox.authentication.repository;

import com.bitbox.authentication.entity.InvitedEmail;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

// TODO : use RedisTemplate
public interface InvitedEmailRepository extends CrudRepository<InvitedEmail, String> {
    Optional<InvitedEmail> findByEmail(String email);
}
