package com.JobPortal.JobPortalBackend.DTO;


import com.JobPortal.JobPortalBackend.Model.JobPost;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
@Data
@RequiredArgsConstructor
public class RecruiterDTO {

    private String profileId;


    private String fullName;

    private String companyName;

    private List<String> jobPosts;


    public void setJobPosts(List<JobPost> jobPosts) {
        this.jobPosts = jobPosts.stream().map(JobPost::getJobId).toList();
    }
}
