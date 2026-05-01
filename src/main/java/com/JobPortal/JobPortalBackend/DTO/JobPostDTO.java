package com.JobPortal.JobPortalBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class JobPostDTO {

    private String jobId;

    private String title;

    private String companyName;

    private String location;

    private Instant postedDate;
}
