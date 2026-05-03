package com.JobPortal.JobPortalBackend.Controller;

import com.JobPortal.JobPortalBackend.DTO.RecruiterDTO;
import com.JobPortal.JobPortalBackend.Model.RecruiterProfile;
import com.JobPortal.JobPortalBackend.Services.RecruiterProfileService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recruiter")
public class RecruiterController {

    private final RecruiterProfileService recruiterProfileService;

    RecruiterController(RecruiterProfileService recruiterProfileService){
        this.recruiterProfileService=recruiterProfileService;
    }


    @GetMapping()
    public RecruiterDTO getRecruiterProfile(@RequestParam(required = false) String profileId) {
        return recruiterProfileService.getProfileByUserId(profileId);
    }

    @PutMapping()
    public RecruiterDTO updateProfile( @Valid @RequestBody RecruiterProfile recruiterProfile){
        return recruiterProfileService.updateProfile(recruiterProfile);
    }



}
