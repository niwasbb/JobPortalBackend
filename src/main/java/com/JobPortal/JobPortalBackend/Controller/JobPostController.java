package com.JobPortal.JobPortalBackend.Controller;

import com.JobPortal.JobPortalBackend.DTO.JobPostRequest;
import com.JobPortal.JobPortalBackend.DTO.JobPostResponse;
import com.JobPortal.JobPortalBackend.Services.JobPostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/jobs")
public class JobPostController {

    private final JobPostService jobPostService;
    @Autowired
    public JobPostController(JobPostService jobPostService){
        this.jobPostService=jobPostService;
    }



    @GetMapping()
    public Page<JobPostResponse> getJobs(@RequestParam(required = false) String searchBy,
                                         @RequestParam(required = false, defaultValue = "1") int pageNo,
                                         @RequestParam(required = false, defaultValue = "10") int pageSize,
                                         @RequestParam(required = false, defaultValue = "postedDate") String sortBy,
                                         @RequestParam(required = false, defaultValue = "DESC") String sortDir)
    {

        Sort sort;
        if(sortDir.equalsIgnoreCase("ASC")){
            sort=Sort.by(sortBy).ascending();
        }
        else{
            sort=Sort.by(sortBy).descending();
        }

        Pageable pageable= PageRequest.of(pageNo-1,pageSize,sort);

        return jobPostService.getJobs(searchBy,pageable);

    }

    @GetMapping("/{jobPostId}")
    public JobPostResponse getJobPostById(@PathVariable UUID jobPostId){
        return jobPostService.getJobPostById(jobPostId);
    }

    @PostMapping()
    public JobPostResponse postNewJob(@Valid @RequestBody JobPostRequest newNobPost){

        return jobPostService.postNewJob(newNobPost);
    }

    @PutMapping("/{jobPostId}")
    public JobPostResponse updateJobPost(@PathVariable UUID jobPostId, @Valid @RequestBody JobPostRequest updatedJobPost){
        return jobPostService.updateJobPost(jobPostId,updatedJobPost);
    }

    @DeleteMapping("/{jobPostId}")
    public ResponseEntity<String> deleteJobPost(@PathVariable UUID jobPostId){
        return jobPostService.deleteJobPost(jobPostId);
    }


}
