package com.bitbox.authentication.repository;

import com.bitbox.authentication.entity.InvitedEmail;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface InvitedEmailRepository extends CrudRepository<InvitedEmail, String> {
    Optional<InvitedEmail> findInvitedEmailByEmail(String email);
}
