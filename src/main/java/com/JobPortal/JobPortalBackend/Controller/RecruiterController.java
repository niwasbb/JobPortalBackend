package com.JobPortal.JobPortalBackend.Controller;

import com.JobPortal.JobPortalBackend.DTO.RecruiterRequest;
import com.JobPortal.JobPortalBackend.DTO.RecruiterResponse;
import com.JobPortal.JobPortalBackend.Services.RecruiterProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/recruiter")
public class RecruiterController {

    private final RecruiterProfileService recruiterProfileService;

    @Autowired
    RecruiterController(RecruiterProfileService recruiterProfileService){
        this.recruiterProfileService=recruiterProfileService;
    }

    @GetMapping()
    public RecruiterResponse getMyProfile(){
        return recruiterProfileService.getMyProfile();
    }

    @GetMapping("/{profileId}")
    public RecruiterResponse getRecruiterProfile(@PathVariable UUID profileId) {
        return recruiterProfileService.getProfileByUserId(profileId);
    }

    @PutMapping()
    public RecruiterResponse updateProfile(@Valid @RequestBody RecruiterRequest recruiterProfile){
        return recruiterProfileService.updateProfile(recruiterProfile);
    }



}
