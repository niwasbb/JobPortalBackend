package com.JobPortal.JobPortalBackend.DTO;


import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class RecruiterDTO {

    private String profileId;


    private String fullName;

    private String companyName;

    private List<JobPostDTO> jobPosts;

}
