package com.bitbox.authentication.repository;

import com.bitbox.authentication.entity.AuthMember;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AuthMemberRepository extends CrudRepository<AuthMember, String> {
    Optional<AuthMember> findAuthMemberByMemberEmailAndDeletedIsFalse(String memberEmail);
}
