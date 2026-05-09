package com.JobPortal.JobPortalBackend.Services;

import com.JobPortal.JobPortalBackend.DTO.RecruiterRequest;
import com.JobPortal.JobPortalBackend.DTO.RecruiterResponse;
import com.JobPortal.JobPortalBackend.Exception.UserNotFoundException;
import com.JobPortal.JobPortalBackend.Model.Recruiter;
import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.RecruiterProfileRepo;
import com.JobPortal.JobPortalBackend.SecurityLayer.AuthenticationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class RecruiterProfileService {

    private final RecruiterProfileRepo recruiterProfileRepo;
    @Autowired
    private ModelMapper modelMapper;
    private final AuthenticationService authenticationService;


    @Autowired
    public RecruiterProfileService(RecruiterProfileRepo recruiterProfileRepo, AuthenticationService authenticationService){
        this.recruiterProfileRepo=recruiterProfileRepo;
        this.authenticationService=authenticationService;
    }


    public void createProfile(Users user, Recruiter profile) {

        profile.setProfileId(null);
        profile.setUser(user);
        profile.setEmailId(user.getEmailId());
        user.setRecruiter(profile);
        recruiterProfileRepo.save(profile);

    }
    public RecruiterResponse getMyProfile() {

        Users user= authenticationService.getLoggedInUser();
        Recruiter recruiter =recruiterProfileRepo.findByUserUserId(user.getUserId()).orElseThrow(()->new UserNotFoundException("Profile not found"));

        return modelMapper.map(recruiter, RecruiterResponse.class);

    }

    public RecruiterResponse getProfileByUserId(UUID profileId){

        Recruiter recruiter =recruiterProfileRepo.findById(profileId).orElseThrow(()->new UserNotFoundException("Profile not found"));

        return modelMapper.map(recruiter, RecruiterResponse.class);
    }


    public RecruiterResponse updateProfile(RecruiterRequest updatedProfile){

        Users loggedInUser= authenticationService.getLoggedInUser();
        Recruiter existingProfile= recruiterProfileRepo.findByUserUserId(loggedInUser.getUserId()).orElseThrow(()->new UserNotFoundException("Profile not found"));

        existingProfile.setFirstName(updatedProfile.getFirstName());
        existingProfile.setLastName(updatedProfile.getLastName());
        existingProfile.setCompanyName(updatedProfile.getCompanyName());
        existingProfile.setIndustryType(updatedProfile.getIndustryType());

        existingProfile= recruiterProfileRepo.save(existingProfile);

        return modelMapper.map(existingProfile, RecruiterResponse.class);


    }


}
