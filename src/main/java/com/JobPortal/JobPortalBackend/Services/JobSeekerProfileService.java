package com.JobPortal.JobPortalBackend.Services;

import com.JobPortal.JobPortalBackend.DTO.JobSeekerRequest;
import com.JobPortal.JobPortalBackend.DTO.JobSeekerResponse;
import com.JobPortal.JobPortalBackend.Exception.UserNotFoundException;
import com.JobPortal.JobPortalBackend.Model.JobSeeker;
import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.JobSeekerProfileRepo;
import com.JobPortal.JobPortalBackend.SecurityLayer.AuthenticationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class JobSeekerProfileService {

    private final JobSeekerProfileRepo jobSeekerProfileRepo;
    @Autowired
    private ModelMapper modelMapper;
    private final AuthenticationService authenticationService;

    @Autowired
    public JobSeekerProfileService(JobSeekerProfileRepo jobSeekerProfileRepo, AuthenticationService authenticationService) {
        this.jobSeekerProfileRepo = jobSeekerProfileRepo;
        this.authenticationService=authenticationService;

    }



    public void createProfile(Users user, JobSeeker profile) {

        profile.setProfileId(null);
        profile.setUser(user);
        profile.setEmailId(user.getEmailId());
        user.setJobSeeker(profile);
        jobSeekerProfileRepo.save(profile);
    }


    public JobSeekerResponse getMyProfile() {

        Users user=authenticationService.getLoggedInUser();
        JobSeeker jobSeeker =jobSeekerProfileRepo.findByUserUserId(user.getUserId()).orElseThrow(() -> new UserNotFoundException( "Profile not found"));

        return modelMapper.map(jobSeeker, JobSeekerResponse.class);

    }

    public JobSeekerResponse getProfileById(UUID profileId) {

        JobSeeker jobSeeker = jobSeekerProfileRepo.findById(profileId).orElseThrow(() -> new UserNotFoundException( "Profile not found"));

        return modelMapper.map(jobSeeker, JobSeekerResponse.class);
    }




    public JobSeekerResponse updateProfile(JobSeekerRequest updatedProfile) {

        Users loggedInUser=authenticationService.getLoggedInUser();

        JobSeeker existingProfile = jobSeekerProfileRepo.findByUserUserId(loggedInUser.getUserId()).orElseThrow(() -> new UserNotFoundException( "Profile not found"));

        existingProfile.setFirstName(updatedProfile.getFirstName());
        existingProfile.setLastName(updatedProfile.getLastName());
        existingProfile.setPhoneNumber(updatedProfile.getPhoneNumber());
        existingProfile.setLocation(updatedProfile.getLocation());
        existingProfile.setSkills(updatedProfile.getSkills());
        existingProfile.setEducation(updatedProfile.getEducation());
        existingProfile.setExperience(updatedProfile.getExperience());
        existingProfile=jobSeekerProfileRepo.save(existingProfile);

        return modelMapper.map(existingProfile, JobSeekerResponse.class);
    }


}
