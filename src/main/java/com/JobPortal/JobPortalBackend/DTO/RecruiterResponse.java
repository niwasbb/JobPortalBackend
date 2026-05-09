package com.JobPortal.JobPortalBackend.DTO;


import com.JobPortal.JobPortalBackend.Model.JobPost;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
@Data
@RequiredArgsConstructor
public class RecruiterResponse {

    private UUID profileId;

    private String firstName;

    private String lastName;

    private String companyName;

    private String  industryType;

    private List<UUID> jobPosts;

    public RecruiterResponse setJobPosts(List<JobPost> jobPosts){
        this.jobPosts=jobPosts.stream().map(JobPost::getJobId).toList();
        return this;
    }

}
