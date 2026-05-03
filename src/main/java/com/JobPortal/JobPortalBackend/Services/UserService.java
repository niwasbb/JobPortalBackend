package com.JobPortal.JobPortalBackend.Services;

import com.JobPortal.JobPortalBackend.Exception.UserNotFoundException;
import com.JobPortal.JobPortalBackend.Model.JobSeekerProfile;
import com.JobPortal.JobPortalBackend.Model.RecruiterProfile;
import com.JobPortal.JobPortalBackend.Model.UserRole;
import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {


    /*************  Dependencies       ********************************************************************************/

    private final UserRepo userRepo;
    private final AuthenticationManager authManager;
    private final JWTService jwtService;
    private final JobSeekerProfileService jobSeekerProfileService;
    private final RecruiterProfileService recruiterProfileService;

    @Autowired
    public UserService(UserRepo userRepo, AuthenticationManager authManager,
                       JWTService jwtService, JobSeekerProfileService jobSeekerProfileService,
                        RecruiterProfileService recruiterProfileService){
        this.userRepo=userRepo;
        this.authManager=authManager;
        this.jwtService=jwtService;
        this.jobSeekerProfileService=jobSeekerProfileService;
        this.recruiterProfileService=recruiterProfileService;
    }
    /******************************************************************************************************************/



//    new user registration

    public void newUser(Users user){

        if (userRepo.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        if(user.getRole()==null || user.getRole()==UserRole.JOB_SEEKER){

            user.setRole(UserRole.JOB_SEEKER);
            Users newUser=userRepo.save(user);
            JobSeekerProfile jobSeekerProfile=new JobSeekerProfile();
            jobSeekerProfileService.createProfile(newUser, jobSeekerProfile);
        }

        else{

            Users newUser=userRepo.save(user);
            RecruiterProfile recruiterProfile= new RecruiterProfile();
            recruiterProfileService.createProfile(newUser,recruiterProfile);
        }

    }


//    verifying login and generating JWT token

    public String userLogin(Users user){

        if(!userRepo.existsByUsername(user.getUsername())) {
            throw new UserNotFoundException("User not found");
        }

        Authentication authentication=authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));

        if(authentication.isAuthenticated()){
            return jwtService.generateToken(user.getUsername());
        }
        return "Credentials not match";
    }


//    Delete user by userId

    public String deleteUserById() {
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = auth.getName();

        Users loggedInUser=userRepo.findByUsername(loggedInUsername).orElseThrow(()->new AuthenticationCredentialsNotFoundException("Authentication problem"));

        String username=loggedInUser.getUsername();
        userRepo.deleteById(loggedInUser.getUserId());

        return username;

    }



}