package com.JobPortal.JobPortalBackend.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class JobPostRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String companyName;

    @NotBlank
    private String location;

    private String jobDescription;

    @NotBlank
    private String requiredSkills;

    @NotBlank
    private String requiredEducation;

    private int noOfVacancy;

    private String salaryRange;
}
