package com.JobPortal.JobPortalBackend.Services;

import com.JobPortal.JobPortalBackend.DTO.JobSeekerRequest;
import com.JobPortal.JobPortalBackend.DTO.JobSeekerResponse;
import com.JobPortal.JobPortalBackend.Exception.UserNotFoundException;
import com.JobPortal.JobPortalBackend.Model.JobSeeker;
import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.JobSeekerProfileRepo;
import com.JobPortal.JobPortalBackend.SecurityService.AuthenticationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@Slf4j
@AllArgsConstructor
public class JobSeekerProfileService {

    private final JobSeekerProfileRepo jobSeekerProfileRepo;
    private final ModelMapper modelMapper;
    private final AuthenticationService authenticationService;

    public void createProfile(Users user, JobSeeker profile) {
        log.info("Creating profile for userId: {}", user.getUserId());


        profile.setProfileId(null);
        profile.setUser(user);
        profile.setEmailId(user.getEmailId());
        user.setJobSeeker(profile);
        jobSeekerProfileRepo.save(profile);

        log.info("Profile created successfully. UserId: {}", user.getUserId());
    }


    public JobSeekerResponse getMyProfile() {

        Users user=authenticationService.getLoggedInUser();
        log.info("Fetching profile for logged-in userId: {}", user.getUserId());

        JobSeeker jobSeeker =jobSeekerProfileRepo.findByUserUserId(user.getUserId()).orElseThrow(() ->{
            log.warn("Profile not found for userId: {}", user.getUserId());
            return new UserNotFoundException( "Profile not found");
            });

        log.info("Profile fetched successfully for userId: {}", user.getUserId());

        return modelMapper.map(jobSeeker, JobSeekerResponse.class);

    }

    public JobSeekerResponse getProfileById(UUID profileId) {

        log.info("Fetching profile by profileId: {}", profileId);

        JobSeeker jobSeeker = jobSeekerProfileRepo.findById(profileId).orElseThrow(() ->{
            log.warn("Profile not found for profileId: {}", profileId);
            return new UserNotFoundException( "Profile not found");
        });

        log.info("Profile fetched successfully for profileId: {}", profileId);

        return modelMapper.map(jobSeeker, JobSeekerResponse.class);
    }




    public JobSeekerResponse updateProfile(JobSeekerRequest updatedProfile) {

        Users loggedInUser=authenticationService.getLoggedInUser();

        log.info("Updating profile for userId: {}", loggedInUser.getUserId());

        JobSeeker existingProfile = jobSeekerProfileRepo.findByUserUserId(loggedInUser.getUserId()).orElseThrow(() ->
                    {   log.warn("Profile not found for userId: {}", loggedInUser.getUserId());
                        return new UserNotFoundException( "Profile not found");
                    });

        existingProfile.setFirstName(updatedProfile.getFirstName());
        existingProfile.setLastName(updatedProfile.getLastName());
        existingProfile.setPhoneNumber(updatedProfile.getPhoneNumber());
        existingProfile.setLocation(updatedProfile.getLocation());
        existingProfile.setSkills(updatedProfile.getSkills());
        existingProfile.setEducation(updatedProfile.getEducation());
        existingProfile.setExperience(updatedProfile.getExperience());
        existingProfile=jobSeekerProfileRepo.save(existingProfile);

        log.info("Profile updated successfully. ProfileId: {}, UserId: {}", existingProfile.getProfileId(), loggedInUser.getUserId());

        return modelMapper.map(existingProfile, JobSeekerResponse.class);
    }


}
