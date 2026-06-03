package com.JobPortal.JobPortalBackend.Services;

import com.JobPortal.JobPortalBackend.DTO.RecruiterRequest;
import com.JobPortal.JobPortalBackend.DTO.RecruiterResponse;
import com.JobPortal.JobPortalBackend.Exception.UserNotFoundException;
import com.JobPortal.JobPortalBackend.Model.Recruiter;
import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.RecruiterProfileRepo;
import com.JobPortal.JobPortalBackend.SecurityService.AuthenticationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@Slf4j
@AllArgsConstructor
public class RecruiterProfileService {

    private final RecruiterProfileRepo recruiterProfileRepo;
    private final ModelMapper modelMapper;
    private final AuthenticationService authenticationService;

    public void createProfile(Users user, Recruiter profile) {
        log.info("Creating recruiter profile for userId: {}", user.getUserId());


        profile.setProfileId(null);
        profile.setUser(user);
        profile.setEmailId(user.getEmailId());
        user.setRecruiter(profile);
        recruiterProfileRepo.save(profile);

        log.info("Recruiter profile created successfully for userId: {}", user.getUserId());


    }
    public RecruiterResponse getMyProfile() {

        Users user= authenticationService.getLoggedInUser();
        log.info("Fetching recruiter profile for logged-in userId: {}", user.getUserId());

        Recruiter recruiter =recruiterProfileRepo.findByUserUserId(user.getUserId()).orElseThrow(()->{
            log.error("Recruiter profile not found for userId: {}", user.getUserId());
            return new UserNotFoundException("Profile not found");
        });

        log.info("Recruiter profile fetched successfully for userId: {}", user.getUserId());

        return modelMapper.map(recruiter, RecruiterResponse.class);

    }

    public RecruiterResponse getProfileByUserId(UUID profileId){
        log.info("Fetching recruiter profile with profileId: {}", profileId);

        Recruiter recruiter =recruiterProfileRepo.findById(profileId).orElseThrow(()->{
            log.error("Recruiter profile not found with profileId: {}", profileId);
            return new UserNotFoundException("Profile not found");
        });
        log.info("Recruiter profile fetched successfully with profileId: {}", profileId);

        return modelMapper.map(recruiter, RecruiterResponse.class);
    }


    public RecruiterResponse updateProfile(RecruiterRequest updatedProfile){

        Users loggedInUser= authenticationService.getLoggedInUser();
        log.info("Updating recruiter profile for userId: {}", loggedInUser.getUserId());

        Recruiter existingProfile= recruiterProfileRepo.findByUserUserId(loggedInUser.getUserId()).orElseThrow(()->{
            log.error("Recruiter profile not found for userId: {}", loggedInUser.getUserId());
            return new UserNotFoundException("Profile not found");
        });

        existingProfile.setFirstName(updatedProfile.getFirstName());
        existingProfile.setLastName(updatedProfile.getLastName());
        existingProfile.setCompanyName(updatedProfile.getCompanyName());
        existingProfile.setIndustryType(updatedProfile.getIndustryType());

        existingProfile= recruiterProfileRepo.save(existingProfile);
        log.info("Recruiter profile updated successfully. ProfileId: {}, UserId: {}", existingProfile.getProfileId(), loggedInUser.getUserId());

        return modelMapper.map(existingProfile, RecruiterResponse.class);


    }


}
