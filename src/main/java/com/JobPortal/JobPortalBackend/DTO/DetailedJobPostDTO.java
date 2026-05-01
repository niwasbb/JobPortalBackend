package com.JobPortal.JobPortalBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class DetailedJobPostDTO {

    private String jobId;
    private String title;
    private String companyName;
    private String location;
    private String jobDescription;
    private String requiredSkills;
    private String requiredEducation;
    private int noOfVacancy;
    private String salaryRange;
    private Instant postedDate;

}
