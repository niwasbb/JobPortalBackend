package com.JobPortal.JobPortalBackend.Services;

import com.JobPortal.JobPortalBackend.DTO.RecruiterDTO;
import com.JobPortal.JobPortalBackend.Model.RecruiterProfile;
import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.RecruiterProfileRepo;
import com.JobPortal.JobPortalBackend.Repository.UserRepo;
import jakarta.annotation.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class RecruiterProfileService {

    private final RecruiterProfileRepo recruiterProfileRepo;
    private final ModelMapper modelMapper;
    private final UserRepo userRepo;


    @Autowired
    public RecruiterProfileService(RecruiterProfileRepo recruiterProfileRepo,
                                    ModelMapper modelMapper,UserRepo userRepo){
        this.recruiterProfileRepo=recruiterProfileRepo;
        this.modelMapper=modelMapper;
        this.userRepo=userRepo;
    }


    public void createProfile(Users user, RecruiterProfile profile) {

        profile.setProfileId(null);
        profile.setUser(user);
        user.setRecruiterProfile(profile);
        recruiterProfileRepo.save(profile);

    }


    public RecruiterDTO getProfileByUserId(@Nullable String profileId){
        RecruiterProfile recruiterProfile;
        if(profileId ==null || profileId.isEmpty()){
            Authentication auth= SecurityContextHolder.getContext().getAuthentication();
            recruiterProfile=recruiterProfileRepo.findById(profileId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Profile not found"));
            return modelMapper.map(recruiterProfile, RecruiterDTO.class);
        }
        recruiterProfile=recruiterProfileRepo.findById(profileId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Profile not found"));
        return modelMapper.map(recruiterProfile, RecruiterDTO.class);
    }


    public RecruiterDTO updateProfile( RecruiterProfile updatedProfile){

        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = auth.getName();

        Users loggedInUser=userRepo.findByUsername(loggedInUsername).orElseThrow(()->new AuthenticationCredentialsNotFoundException("Authentication problem"));

        RecruiterProfile existingProfile= loggedInUser.getRecruiterProfile();

        existingProfile.setFullName(updatedProfile.getFullName());
        existingProfile.setCompanyName(updatedProfile.getCompanyName());

        existingProfile= recruiterProfileRepo.save(existingProfile);
        return modelMapper.map(existingProfile,RecruiterDTO.class);


    }

}
