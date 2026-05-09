package com.JobPortal.JobPortalBackend.Repository;

import com.JobPortal.JobPortalBackend.Model.JobSeeker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobSeekerProfileRepo extends JpaRepository<JobSeeker, UUID> {

    Optional<JobSeeker> findByUserUserId(UUID userId);

}
