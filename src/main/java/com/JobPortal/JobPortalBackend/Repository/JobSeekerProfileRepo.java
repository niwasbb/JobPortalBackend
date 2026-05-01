package com.JobPortal.JobPortalBackend.Repository;

import com.JobPortal.JobPortalBackend.Model.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobSeekerProfileRepo extends JpaRepository<JobSeekerProfile, String> {

    Optional<JobSeekerProfile> findByUserUserId(String userId);

    boolean existsByUserUserId(String userId);
}
