package com.JobPortal.JobPortalBackend.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

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

    private List<String> requiredSkills;

    private String requiredEducation;

    private String requiredExperience;

    private int noOfVacancy;

    private String salaryRange;
}
