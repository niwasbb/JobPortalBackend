package com.JobPortal.JobPortalBackend.DTO;

import com.JobPortal.JobPortalBackend.Model.JobApplication;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class JobSeekerResponse {

    private UUID profileId;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    @Email
    private String emailId;

    private String location;

    private List<String> skills;

    private String education;

    private String experience;

    private String resume;

    private List<UUID> appliedJobs;

    public JobSeekerResponse setAppliedJobs(List<JobApplication> jobApplications){
        this.appliedJobs=jobApplications.stream().map(JobApplication::getApplicationId).toList();
        return this;
    }

}
