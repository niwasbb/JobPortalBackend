package com.JobPortal.JobPortalBackend.Services;

import com.JobPortal.JobPortalBackend.DTO.JobSeekerDTO;
import com.JobPortal.JobPortalBackend.Model.JobSeekerProfile;
import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.JobSeekerProfileRepo;
import com.JobPortal.JobPortalBackend.Repository.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class JobSeekerProfileService {

    private final JobSeekerProfileRepo jobSeekerProfileRepo;
    private final ModelMapper modelMapper;
    private final UserRepo userRepo;

    @Autowired
    public JobSeekerProfileService(JobSeekerProfileRepo jobSeekerProfileRepo,
                                   ModelMapper modelMapper, UserRepo userRepo){
        this.jobSeekerProfileRepo = jobSeekerProfileRepo;
        this.modelMapper=modelMapper;
        this.userRepo= userRepo;

    }



    public void createProfile(Users user, JobSeekerProfile profile) {

        profile.setProfileId(null);
        profile.setUser(user);
        profile.setEmail(user.getEmail());
        user.setJobSeekerProfile(profile);
        jobSeekerProfileRepo.save(profile);
    }



    public JobSeekerDTO getProfileByUserId(String profileId) {
        JobSeekerProfile jobSeekerProfile;

        if(profileId ==null || profileId.isEmpty()){
            Authentication auth= SecurityContextHolder.getContext().getAuthentication();
            jobSeekerProfile=userRepo.findById(profileId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Profile not found")).getJobSeekerProfile();
            return modelMapper.map(jobSeekerProfile, JobSeekerDTO.class);
        }
        jobSeekerProfile= jobSeekerProfileRepo.findById(profileId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
        return modelMapper.map(jobSeekerProfile, JobSeekerDTO.class);
    }




    public JobSeekerDTO updateProfile( JobSeekerProfile updatedProfile) {

        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = auth.getName();

        Users loggedInUser=userRepo.findByUsername(loggedInUsername).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Profile not found"));

        JobSeekerProfile existingProfile = loggedInUser.getJobSeekerProfile();

        existingProfile.setFullName(updatedProfile.getFullName());
        existingProfile.setPhoneNumber(updatedProfile.getPhoneNumber());
        existingProfile.setLocation(updatedProfile.getLocation());
        existingProfile.setSkills(updatedProfile.getSkills());
        existingProfile.setEducation(updatedProfile.getEducation());
        existingProfile.setExperience(updatedProfile.getExperience());
        existingProfile.setResumeUrl(updatedProfile.getResumeUrl());
        existingProfile=jobSeekerProfileRepo.save(existingProfile);
        return modelMapper.map(existingProfile, JobSeekerDTO.class);
    }


}
