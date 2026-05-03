package com.JobPortal.JobPortalBackend.Repository;

import com.JobPortal.JobPortalBackend.Model.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepo extends JpaRepository<JobApplication, String> {

    @Query("SELECT ja FROM JobApplication ja WHERE ja.jobPost.jobId = :jobId")
    Page<JobApplication> findAllByJobId(String jobId, Pageable pageable);

    Page<JobApplication> findAllByJobSeekerProfile_ProfileId(String profileId, Pageable pageable);
}
