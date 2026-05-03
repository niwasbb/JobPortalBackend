package com.JobPortal.JobPortalBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class JobPostDTO {


    private String jobId;
    private String title;
    private LocalDateTime postedDate;
    private String companyName;
    private String location;
    private String jobDescription;
    private String requiredSkills;
    private String requiredEducation;
    private int noOfVacancy;
    private String salaryRange;

}
