package com.JobPortal.JobPortalBackend.Repository;

import com.JobPortal.JobPortalBackend.Model.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepo extends JpaRepository<JobApplication, UUID> {

    Page<JobApplication> findAllByJobPostJobId(UUID jobId, Pageable pageable);

    Page<JobApplication> findAllByJobSeekerProfileId(UUID profileId, Pageable pageable);

    Optional<JobApplication> findByJobPostJobIdAndJobSeekerProfileId(UUID jobPostId, UUID profileId);

    Optional<JobApplication> findByApplicationIdAndJobSeekerProfileId(UUID applicationId, UUID profileId);

    Optional<JobApplication> findByApplicationIdAndJobPostRecruiterProfileId(UUID applicationId, UUID profileId);
}


