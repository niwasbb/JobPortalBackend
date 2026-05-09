package com.JobPortal.JobPortalBackend.Services;

import com.JobPortal.JobPortalBackend.DTO.LoginRequest;
import com.JobPortal.JobPortalBackend.DTO.UserRequest;
import com.JobPortal.JobPortalBackend.DTO.UsersResponse;
import com.JobPortal.JobPortalBackend.Exception.UserAlreadyExistsException;
import com.JobPortal.JobPortalBackend.Exception.UserNotFoundException;
import com.JobPortal.JobPortalBackend.Model.JobSeeker;
import com.JobPortal.JobPortalBackend.Model.Recruiter;
import com.JobPortal.JobPortalBackend.Model.UserRole;
import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.UserRepo;
import com.JobPortal.JobPortalBackend.SecurityLayer.AuthenticationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserService {


    /*************  Dependencies       ********************************************************************************/

    private final UserRepo userRepo;
    private final AuthenticationManager authManager;
    private final JWTService jwtService;
    private final JobSeekerProfileService jobSeekerProfileService;
    private final RecruiterProfileService recruiterProfileService;
    private final AuthenticationService authenticationService;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public UserService(UserRepo userRepo, AuthenticationManager authManager,
                       JWTService jwtService, JobSeekerProfileService jobSeekerProfileService,
                        RecruiterProfileService recruiterProfileService,AuthenticationService authenticationService){
        this.userRepo=userRepo;
        this.authManager=authManager;
        this.jwtService=jwtService;
        this.jobSeekerProfileService=jobSeekerProfileService;
        this.recruiterProfileService=recruiterProfileService;
        this.authenticationService=authenticationService;
    }
    /******************************************************************************************************************/


//    new user registration


    public UsersResponse getUser() {
        Users user=authenticationService.getLoggedInUser();

        return modelMapper.map(user, UsersResponse.class);

    }


    public ResponseEntity<String> newUser(UserRequest user){


        if (userRepo.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException( "account with this username already exists");
        }
        if (userRepo.existsByEmailId(user.getEmailId())) {
            throw new UserAlreadyExistsException( "account with this email already exists");
        }

        Users newUser= new Users();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(user.getPassword());
        newUser.setEmailId(user.getEmailId());

        if(user.getRole()==null || user.getRole()==UserRole.JOB_SEEKER){

            user.setRole(UserRole.JOB_SEEKER);
            newUser.setRole(user.getRole());
            newUser=userRepo.save(newUser);
            JobSeeker jobSeeker =new JobSeeker();
            jobSeekerProfileService.createProfile(newUser, jobSeeker);
            return new ResponseEntity<>("registration done. You can login and Update your profile",HttpStatus.OK);
        }
        else{
            newUser.setRole(user.getRole());
            newUser=userRepo.save(newUser);
            Recruiter recruiter = new Recruiter();
            recruiterProfileService.createProfile(newUser, recruiter);
            return new ResponseEntity<>("registration done. You can login and Update your profile",HttpStatus.OK);

        }

    }


//    verifying login and generating JWT token

    public String userLogin(LoginRequest loginRequest){

        if(!userRepo.existsByUsername(loginRequest.getUsername())) {
            throw new UserNotFoundException("User not found");
        }

        Authentication authentication=authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));

        if(authentication.isAuthenticated()){
            return jwtService.generateToken(loginRequest.getUsername());
        }
        else{
            throw new AuthenticationCredentialsNotFoundException("Invalid login details");
        }
    }


//    Delete user by userId

    public String deleteAccount() {

        Users loggedInUser=authenticationService.getLoggedInUser();
        String username=loggedInUser.getUsername();
        userRepo.deleteById(loggedInUser.getUserId());

        return username;

    }

}