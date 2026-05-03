package com.JobPortal.JobPortalBackend.Controller;

import com.JobPortal.JobPortalBackend.DTO.JobSeekerDTO;
import com.JobPortal.JobPortalBackend.Model.JobSeekerProfile;
import com.JobPortal.JobPortalBackend.Services.JobSeekerProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/jobseeker")
public class JobSeekerProfileController {

    private final JobSeekerProfileService jobSeekerProfileService;

    @Autowired
    public JobSeekerProfileController(JobSeekerProfileService jobSeekerProfileService) {
        this.jobSeekerProfileService = jobSeekerProfileService;
    }


    @GetMapping()
    public JobSeekerDTO getJobSeekerProfile(@RequestParam(required = false) String profileId) {
        return jobSeekerProfileService.getProfileByUserId(profileId);
    }


    @PutMapping()
    public JobSeekerDTO updateProfile( @Valid @RequestBody JobSeekerProfile profile) {
        return jobSeekerProfileService.updateProfile(profile);
    }


}
