package com.JobPortal.JobPortalBackend.DTO;

import com.JobPortal.JobPortalBackend.Model.ApplicationStatus;
import com.JobPortal.JobPortalBackend.Model.JobSeekerProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ApplicationDTO {
    private String applicationId;
    private String jobPostId;
    private String jobSeekerId;
    private ApplicationStatus status;
}
