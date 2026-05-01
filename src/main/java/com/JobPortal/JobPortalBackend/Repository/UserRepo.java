package com.JobPortal.JobPortalBackend.Repository;

import com.JobPortal.JobPortalBackend.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepo extends JpaRepository<Users, String> {



    Optional<Users> findByUsername(String username);
    boolean existsByUsername(String username);

}
