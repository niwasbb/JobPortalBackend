package com.JobPortal.JobPortalBackend.Repository;

import com.JobPortal.JobPortalBackend.Model.RecruiterProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecruiterProfileRepo extends JpaRepository<RecruiterProfile, String> {

    Optional<RecruiterProfile> findByUserUserId(String userId);
}
