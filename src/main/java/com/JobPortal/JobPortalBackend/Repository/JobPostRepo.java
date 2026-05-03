package com.JobPortal.JobPortalBackend.Repository;

import com.JobPortal.JobPortalBackend.Model.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostRepo extends JpaRepository<JobPost, String> {

    @Query("SELECT j FROM JobPost j WHERE " +
            "LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.jobDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.requiredSkills) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.requiredEducation) LIKE LOWER(CONCAT('%', :keyword, '%'))OR "+
            "LOWER(j.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<JobPost> findAll(String keyword, Pageable pageable);
}
