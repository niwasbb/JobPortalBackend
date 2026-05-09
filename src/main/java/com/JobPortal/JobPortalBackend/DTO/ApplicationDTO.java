package com.JobPortal.JobPortalBackend.DTO;

import com.JobPortal.JobPortalBackend.Model.ApplicationStatus;
import com.JobPortal.JobPortalBackend.Model.JobPost;
import com.JobPortal.JobPortalBackend.Model.JobSeeker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ApplicationDTO {
    private UUID applicationId;
    private UUID jobPost;
    private UUID jobSeeker;
    private ApplicationStatus status;
    private String resume;

    public ApplicationDTO setJobPost(JobPost jobPostId) {

        this.jobPost = jobPostId.getJobId();
        return this;
    }

    public ApplicationDTO setJobSeeker(JobSeeker jobSeekerId) {
        this.jobSeeker = jobSeekerId.getProfileId();
        return this;
    }
}
