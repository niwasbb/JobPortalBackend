package com.JobPortal.JobPortalBackend.Repository;

import com.JobPortal.JobPortalBackend.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserRepo extends JpaRepository<Users, UUID> {



    Optional<Users> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmailId(String email);
}
