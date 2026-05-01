package com.JobPortal.JobPortalBackend.Controller;

import com.JobPortal.JobPortalBackend.DTO.RecruiterDTO;
import com.JobPortal.JobPortalBackend.Model.RecruiterProfile;
import com.JobPortal.JobPortalBackend.Services.RecruiterProfileService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recruiter")
public class RecruiterController {

    private final RecruiterProfileService recruiterProfileService;

    RecruiterController(RecruiterProfileService recruiterProfileService){
        this.recruiterProfileService=recruiterProfileService;
    }

    @GetMapping("/{userId}")
    public RecruiterDTO getRecruiterProfile(@PathVariable("userId") String userId) {
        return recruiterProfileService.getProfileByUserId(userId);
    }

    @PutMapping("/{userId}")
    public RecruiterDTO updatProfile(@PathVariable("userId")String userId, @RequestBody RecruiterProfile recruiterProfile){
        return recruiterProfileService.updateProfile(userId,recruiterProfile);
    }
}
