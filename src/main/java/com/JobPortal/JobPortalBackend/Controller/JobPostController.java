package com.JobPortal.JobPortalBackend.Controller;

import com.JobPortal.JobPortalBackend.DTO.DetailedJobPostDTO;
import com.JobPortal.JobPortalBackend.DTO.JobPostDTO;
import com.JobPortal.JobPortalBackend.Model.JobPost;
import com.JobPortal.JobPortalBackend.Services.JobPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
public class JobPostController {

    private final JobPostService jobPostService;
    @Autowired
    public JobPostController(JobPostService jobPostService){
        this.jobPostService=jobPostService;
    }

    @GetMapping("/")
    public Page<JobPostDTO> getJobs(Pageable pageable){

        return jobPostService.getJobs(pageable);

    }

    @GetMapping("/{id}")
    public DetailedJobPostDTO getJobPost(@PathVariable String id){

        return jobPostService.getJobPostById(id);
    }

    @PostMapping("/post_new_job")
    public DetailedJobPostDTO postNewJob(@RequestBody DetailedJobPostDTO newNobPost){

        return jobPostService.postNewJob(newNobPost);
    }
}
