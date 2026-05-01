package com.JobPortal.JobPortalBackend.Services;

import com.JobPortal.JobPortalBackend.DTO.JobSeekerDTO;
import com.JobPortal.JobPortalBackend.Model.JobSeekerProfile;
import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.JobSeekerProfileRepo;
import com.JobPortal.JobPortalBackend.Repository.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class JobSeekerProfileService {

    private final JobSeekerProfileRepo jobSeekerProfileRepo;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    @Autowired
    public JobSeekerProfileService(JobSeekerProfileRepo jobSeekerProfileRepo, UserRepo userRepo,
                                    ModelMapper modelMapper) {
        this.jobSeekerProfileRepo = jobSeekerProfileRepo;
        this.userRepo = userRepo;
        this.modelMapper=modelMapper;

    }



    public void createProfile(String userId, JobSeekerProfile profile) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


        profile.setProfileId(null);
        profile.setUser(user);
        profile.setEmail(user.getEmail());
        user.setJobSeekerProfile(profile);
        jobSeekerProfileRepo.save(profile);
    }



    public JobSeekerDTO getProfileByUserId(String userId) {
        JobSeekerProfile jobSeekerProfile= jobSeekerProfileRepo.findByUserUserId(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
        return modelMapper.map(jobSeekerProfile, JobSeekerDTO.class);
    }




    public JobSeekerDTO updateProfile(String userId, JobSeekerProfile updatedProfile) {

        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        String loggedinUsername = auth.getName();

        JobSeekerProfile existingProfile = jobSeekerProfileRepo.findByUserUserId(userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Profile not found"));

        if(!loggedinUsername.equals(existingProfile.getUser().getUsername())){
            throw new AuthorizationServiceException(HttpStatus.FORBIDDEN+" You are not authorized to update this profile");
        }


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
