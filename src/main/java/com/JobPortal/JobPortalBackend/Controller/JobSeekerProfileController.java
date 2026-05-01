package com.JobPortal.JobPortalBackend.Controller;

import com.JobPortal.JobPortalBackend.DTO.JobSeekerDTO;
import com.JobPortal.JobPortalBackend.Model.JobSeekerProfile;
import com.JobPortal.JobPortalBackend.Services.JobSeekerProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/job-seeker-profiles")
public class JobSeekerProfileController {

    private final JobSeekerProfileService jobSeekerProfileService;

    @Autowired
    public JobSeekerProfileController(JobSeekerProfileService jobSeekerProfileService) {
        this.jobSeekerProfileService = jobSeekerProfileService;
    }


    @GetMapping("/{userId}")
    public JobSeekerDTO getProfile(@PathVariable String userId) {
        return jobSeekerProfileService.getProfileByUserId(userId);
    }


    @PutMapping("/{userId}")
    public JobSeekerDTO updateProfile(@PathVariable String userId, @Valid @RequestBody JobSeekerProfile profile) {
        return jobSeekerProfileService.updateProfile(userId, profile);
    }
}
