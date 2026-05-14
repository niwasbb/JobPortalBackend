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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {


    /*************  Dependencies       ********************************************************************************/

    private final UserRepo userRepo;
    private final AuthenticationManager authManager;
    private final JWTService jwtService;
    private final JobSeekerProfileService jobSeekerProfileService;
    private final RecruiterProfileService recruiterProfileService;
    private final AuthenticationService authenticationService;
    private final ModelMapper modelMapper=new ModelMapper();
    private final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(12);

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

    @Transactional
    public String newUser(UserRequest user){
        user.setPassword(encoder.encode( user.getPassword()));

        if (userRepo.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException( "account with this username already exists");
        }

        if(userRepo.existsByEmailId(user.getEmailId())){
            throw new UserAlreadyExistsException( "account with this emailId already exists");
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
        }
        else{
            newUser.setRole(user.getRole());
            newUser=userRepo.save(newUser);
            Recruiter recruiter = new Recruiter();
            recruiterProfileService.createProfile(newUser, recruiter);

        }
        return "registration done. You can login and Update your profile";

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
            throw new BadCredentialsException("Invalid login details");
        }
    }


//    Delete user by userId

    public String deleteAccount() {

        Users loggedInUser=authenticationService.getLoggedInUser();
        userRepo.deleteById(loggedInUser.getUserId());

        return "Account is deleted successfully";

    }

}