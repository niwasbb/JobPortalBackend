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
import com.JobPortal.JobPortalBackend.SecurityService.AuthenticationService;
import com.JobPortal.JobPortalBackend.SecurityService.JWTService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final AuthenticationManager authManager;
    private final JWTService jwtService;
    private final JobSeekerProfileService jobSeekerProfileService;
    private final RecruiterProfileService recruiterProfileService;
    private final AuthenticationService authenticationService;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(12);



    public UsersResponse getUser() {
        Users user=authenticationService.getLoggedInUser();
        log.info("Fetching profile for user: {}", user.getUsername());
        return modelMapper.map(user, UsersResponse.class);

    }

    @Transactional
    public String newUser(UserRequest user){
        log.info("Registration request received for username: {}", user.getUsername());
        user.setPassword(encoder.encode( user.getPassword()));

        if (userRepo.existsByUsername(user.getUsername())) {
            log.warn("Registration failed. Username already exists: {}",user.getUsername());
            throw new UserAlreadyExistsException( "account with this username already exists");
        }

        if(userRepo.existsByEmailId(user.getEmailId())){
            log.warn("Registration failed. Email already exists: {}", user.getEmailId());
            throw new UserAlreadyExistsException( "account with this emailId already exists");
        }
        Users newUser= new Users();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(user.getPassword());
        newUser.setEmailId(user.getEmailId());

        if(user.getRole()==null || user.getRole()==UserRole.JOB_SEEKER){
            log.debug("Creating Job Seeker profile for user: {}", user.getUsername());

            user.setRole(UserRole.JOB_SEEKER);
            newUser.setRole(user.getRole());
            newUser=userRepo.save(newUser);
            JobSeeker jobSeeker =new JobSeeker();
            jobSeekerProfileService.createProfile(newUser, jobSeeker);
        }
        else{
            log.debug("Creating Recruiter profile for user: {}", user.getUsername());

            newUser.setRole(user.getRole());
            newUser=userRepo.save(newUser);
            Recruiter recruiter = new Recruiter();
            recruiterProfileService.createProfile(newUser, recruiter);

        }
        log.info("User registered successfully. UserId: {}, Username: {}, Role: {}", newUser.getUserId(), newUser.getUsername(), newUser.getRole());
        return "registration done. You can login and Update your profile";

    }


//    verifying login and generating JWT token

    public String userLogin(LoginRequest loginRequest){
        log.info("Login attempt for username: {}", loginRequest.getUsername());

        if(!userRepo.existsByUsername(loginRequest.getUsername())) {
            log.warn("Login failed. User not found: {}", loginRequest.getUsername());
            throw new UserNotFoundException("User not found");
        }

        Authentication authentication=authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));

        if(authentication.isAuthenticated()){
            log.info("Login successful for username: {}", loginRequest.getUsername());
            return jwtService.generateToken(loginRequest.getUsername());


        }
        else{
            log.warn("Login failed due to invalid credentials for username: {}", loginRequest.getUsername());

            throw new BadCredentialsException("Invalid login details");
        }
    }


//    Delete user by userId

    public String deleteAccount() {

        Users loggedInUser=authenticationService.getLoggedInUser();
        log.info("Account deletion requested by userId: {}, username: {}", loggedInUser.getUserId(), loggedInUser.getUsername());

        userRepo.deleteById(loggedInUser.getUserId());
        log.info("Account deleted successfully. userId: {}, username: {}", loggedInUser.getUserId(), loggedInUser.getUsername());

        return "Account is deleted successfully";

    }

}