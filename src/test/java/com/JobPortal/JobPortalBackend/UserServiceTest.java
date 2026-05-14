package com.JobPortal.JobPortalBackend;

import com.JobPortal.JobPortalBackend.DTO.LoginRequest;
import com.JobPortal.JobPortalBackend.DTO.UserRequest;
import com.JobPortal.JobPortalBackend.Exception.UserAlreadyExistsException;
import com.JobPortal.JobPortalBackend.Exception.UserNotFoundException;
import com.JobPortal.JobPortalBackend.Model.UserRole;
import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.UserRepo;
import com.JobPortal.JobPortalBackend.SecurityLayer.AuthenticationService;
import com.JobPortal.JobPortalBackend.Services.JWTService;
import com.JobPortal.JobPortalBackend.Services.JobSeekerProfileService;
import com.JobPortal.JobPortalBackend.Services.RecruiterProfileService;
import com.JobPortal.JobPortalBackend.Services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepo userRepo;
    @Mock
    private AuthenticationManager authManager;
    @Mock
    private JWTService jwtService;
    @Mock
    private JobSeekerProfileService jobSeekerProfileService;
    @Mock
    private RecruiterProfileService recruiterProfileService;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    Authentication authentication;

    @Test
    public void newUserAlreadyExistsByUsernameTest(){
        //'Arrange data'
        UserRequest userRequest=new UserRequest();
        userRequest.setUsername("AbcdXyz");

        // 'act'
        when(userRepo.existsByUsername(userRequest.getUsername())).thenReturn(true);

        //'assert'
        Assertions.assertThrows(UserAlreadyExistsException.class,()->userService.newUser(userRequest));
        verify(userRepo,never()).save(any());

    }

    @Test
    public void newUserAlreadyExistsByEmailIdTest(){
        //'Arrange data'
        UserRequest userRequest=new UserRequest();
        userRequest.setEmailId("AbcdXyz@email.com");

        // 'act'
        when(userRepo.existsByUsername(userRequest.getUsername())).thenReturn(false);
        when(userRepo.existsByEmailId(userRequest.getEmailId())).thenReturn(true);

        //'assert'
        Assertions.assertThrows(UserAlreadyExistsException.class,()->userService.newUser(userRequest));

        verify(userRepo,never()).save(any());

    }

    @Test
    public void newUserWithUserRoleIsNullTest(){
        UserRequest userRequest=new UserRequest();

        when(userRepo.existsByUsername(userRequest.getUsername())).thenReturn(false);
        when(userRepo.existsByEmailId(userRequest.getEmailId())).thenReturn(false);
        when(userRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String response=userService.newUser(userRequest);

        Assertions.assertEquals("registration done. You can login and Update your profile",response);
        verify(userRepo,times(1)).save(any());
        verify(jobSeekerProfileService,times(1)).createProfile(any(),any());
        verify(recruiterProfileService,never()).createProfile(any(),any());
    }

    @Test
    public void newUserWithJobseekerRoleTest(){
        UserRequest userRequest=new UserRequest();
        userRequest.setRole(UserRole.JOB_SEEKER);

        when(userRepo.existsByUsername(userRequest.getUsername())).thenReturn(false);
        when(userRepo.existsByEmailId(userRequest.getEmailId())).thenReturn(false);
        when(userRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        String response=userService.newUser(userRequest);

        Assertions.assertEquals("registration done. You can login and Update your profile",response);
        verify(userRepo,times(1)).save(any());
        verify(jobSeekerProfileService,times(1)).createProfile(any(),any());
        verify(recruiterProfileService,never()).createProfile(any(),any());
    }

    @Test
    public void newUserWithRecruiterRoleTest(){
        UserRequest userRequest=new UserRequest();
        userRequest.setRole(UserRole.RECRUITER);

        when(userRepo.existsByUsername(userRequest.getUsername())).thenReturn(false);
        when(userRepo.existsByEmailId(userRequest.getEmailId())).thenReturn(false);
        when(userRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String response=userService.newUser(userRequest);

        Assertions.assertEquals("registration done. You can login and Update your profile",response);
        verify(userRepo,times(1)).save(any());
        verify(jobSeekerProfileService,never()).createProfile(any(),any());
        verify(recruiterProfileService,times(1)).createProfile(any(),any());
    }

    @Test
    public void userLoginUserNotExistsTest(){
        LoginRequest loginRequest=new LoginRequest();
        loginRequest.setUsername("abcxyz");
        loginRequest.setPassword("abcxyz@123");

        when(!userRepo.existsByUsername(loginRequest.getUsername())).thenReturn(false);

        Assertions.assertThrows(UserNotFoundException.class,()->userService.userLogin(loginRequest));

    }
    @Test
    public void userLoginAuthenticationSuccessTest(){
        LoginRequest loginRequest=new LoginRequest();
        loginRequest.setUsername("abcxyz");
        loginRequest.setPassword("abcxyz@123");

        when(!userRepo.existsByUsername(loginRequest.getUsername())).thenReturn(true);
        when(authManager.authenticate(any())).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken(loginRequest.getUsername())).thenReturn("Jwt-Token");

        String token=userService.userLogin(loginRequest);

        Assertions.assertEquals("Jwt-Token",token);
        verify(jwtService,times(1)).generateToken(loginRequest.getUsername());

    }

    @Test
    public void userLoginAuthenticationFailerTest(){
        LoginRequest loginRequest=new LoginRequest();
        loginRequest.setUsername("abcxyz");
        loginRequest.setPassword("abcxyz@123");

        when(!userRepo.existsByUsername(loginRequest.getUsername())).thenReturn(true);
        when(authManager.authenticate(any())).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        Assertions.assertThrows(BadCredentialsException.class,()->userService.userLogin(loginRequest));
        verify(jwtService, never()).generateToken(any());

    }

    @Test
    public void deleteAccountTest(){
        Users user=new Users();
        user.setUserId(UUID.randomUUID());
        when(authenticationService.getLoggedInUser()).thenReturn(user);

        String s=userService.deleteAccount();
        verify(userRepo,times(1)).deleteById(user.getUserId());

    }

}