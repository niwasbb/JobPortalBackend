package com.JobPortal.JobPortalBackend.Repository;

import com.JobPortal.JobPortalBackend.Model.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostRepo extends JpaRepository<JobPost, String> {

    Page<JobPost> findAll(Pageable pageable);
}
