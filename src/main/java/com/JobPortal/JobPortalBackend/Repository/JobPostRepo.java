package com.JobPortal.JobPortalBackend.Repository;

import com.JobPortal.JobPortalBackend.Model.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobPostRepo extends JpaRepository<JobPost, UUID> {

    @Query("""
                SELECT DISTINCT j
                FROM JobPost j
                LEFT JOIN j.requiredSkills skill
                WHERE LOWER(j.requiredEducation) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(j.jobDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(j.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(skill) LIKE LOWER(CONCAT('%', :keyword, '%'))
                """)
    Page<JobPost> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByJobIdAndRecruiterProfileId(UUID jobId, UUID profileId);

    Optional<JobPost> findByJobIdAndRecruiterProfileId(UUID jobPostId, UUID profileId);
}
