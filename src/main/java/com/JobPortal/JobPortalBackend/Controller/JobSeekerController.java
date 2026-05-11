package com.JobPortal.JobPortalBackend.Controller;

import com.JobPortal.JobPortalBackend.DTO.JobSeekerRequest;
import com.JobPortal.JobPortalBackend.DTO.JobSeekerResponse;
import com.JobPortal.JobPortalBackend.Services.JobSeekerProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/jobseeker")
public class JobSeekerController {

    private final JobSeekerProfileService jobSeekerProfileService;

    @Autowired
    public JobSeekerController(JobSeekerProfileService jobSeekerProfileService) {
        this.jobSeekerProfileService = jobSeekerProfileService;
    }

    @GetMapping()
    public JobSeekerResponse getMyProfile(){
        return jobSeekerProfileService.getMyProfile();
    }

    @GetMapping("/{profileId}")
    public JobSeekerResponse getJobSeekerProfile(@PathVariable UUID profileId) {
        return jobSeekerProfileService.getProfileById(profileId);
    }


    @PutMapping()
    public JobSeekerResponse updateProfile(@Valid @RequestBody JobSeekerRequest profile) {
        return jobSeekerProfileService.updateProfile(profile);
    }



}
