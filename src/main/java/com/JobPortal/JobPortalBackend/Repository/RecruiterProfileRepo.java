package com.JobPortal.JobPortalBackend.Repository;

import com.JobPortal.JobPortalBackend.Model.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecruiterProfileRepo extends JpaRepository<Recruiter, UUID> {

    Optional<Recruiter> findByUserUserId(UUID userId);
}
