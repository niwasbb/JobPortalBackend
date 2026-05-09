package com.JobPortal.JobPortalBackend.Repository;

import com.JobPortal.JobPortalBackend.Model.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobPostRepo extends JpaRepository<JobPost, UUID> {

    @Query("SELECT j FROM JobPost j WHERE " +
            "LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.jobDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.requiredSkills) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.requiredEducation) LIKE LOWER(CONCAT('%', :keyword, '%'))OR "+
            "LOWER(j.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<JobPost> findAll(String keyword, Pageable pageable);

    List<JobPost> findAllByRecruiter_ProfileId(UUID profileId);
}
