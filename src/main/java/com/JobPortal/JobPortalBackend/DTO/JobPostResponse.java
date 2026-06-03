package com.JobPortal.JobPortalBackend.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class JobPostResponse {


    private UUID jobId;

    @NotBlank
    private String title;

    private LocalDateTime postedDate;

    @NotBlank
    private String companyName;

    @NotBlank
    private String location;

    private String jobDescription;

    @NotBlank
    private List<String> requiredSkills;

    @NotBlank
    private List<String> requiredEducation;

    @NotBlank
    private List<String> requiredExperience;

    private int noOfVacancy;

    private String salaryRange;

}
