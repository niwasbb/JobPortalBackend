package com.JobPortal.JobPortalBackend.DTO;

import com.JobPortal.JobPortalBackend.Model.JobApplication;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class JobSeekerDTO {

    private String profileId;

    private String fullName;

    private String phoneNumber;

    private String location;

    private List<String> skills;

    private String education;

    private String experience;

    private String resumeUrl;

    private List<String> appliedJobs;

    public void setAppliedJobs(List<JobApplication> appliedJobs) {
        this.appliedJobs = appliedJobs.stream().map(application -> application.getJobPost().getJobId()).toList();
    }
}
