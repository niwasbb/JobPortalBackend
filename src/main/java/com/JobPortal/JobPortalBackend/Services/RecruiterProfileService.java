package com.JobPortal.JobPortalBackend.Services;

import com.JobPortal.JobPortalBackend.DTO.RecruiterDTO;
import com.JobPortal.JobPortalBackend.Model.RecruiterProfile;
import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.RecruiterProfileRepo;
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
public class RecruiterProfileService {

    private final UserRepo userRepo;
    private final RecruiterProfileRepo recruiterProfileRepo;
    private final ModelMapper modelMapper;


    @Autowired
    public RecruiterProfileService(UserRepo userRepo,RecruiterProfileRepo recruiterProfileRepo,
                                    ModelMapper modelMapper){
        this.userRepo=userRepo;
        this.recruiterProfileRepo=recruiterProfileRepo;
        this.modelMapper=modelMapper;
    }


    public void createProfile(String userId, RecruiterProfile profile) {
        Users user= userRepo.findById(userId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));

        profile.setProfileId(null);
        profile.setUser(user);
        user.setRecruiterProfile(profile);
        recruiterProfileRepo.save(profile);

    }


    public RecruiterDTO getProfileByUserId(String userId){
        RecruiterProfile recruiterProfile=recruiterProfileRepo.findByUserUserId(userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Profile not found"));
        return modelMapper.map(recruiterProfile, RecruiterDTO.class);
    }

    public RecruiterDTO updateProfile(String userId, RecruiterProfile updateProfile){
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        String loggedinUsername = auth.getName();


        RecruiterProfile existingProfile= recruiterProfileRepo.findByUserUserId(userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));

        if(!loggedinUsername.equals(existingProfile.getUser().getUsername())){
            throw new AuthorizationServiceException(HttpStatus.FORBIDDEN+" You are not authorized to update this profile");
        }

        existingProfile.setFullName(updateProfile.getFullName());
        existingProfile.setCompanyName(updateProfile.getCompanyName());

        existingProfile= recruiterProfileRepo.save(existingProfile);
        return modelMapper.map(existingProfile,RecruiterDTO.class);


    }

}
