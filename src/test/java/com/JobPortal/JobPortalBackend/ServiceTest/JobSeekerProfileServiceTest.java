package com.JobPortal.JobPortalBackend.ServiceTest;


import com.JobPortal.JobPortalBackend.DTO.JobSeekerRequest;
import com.JobPortal.JobPortalBackend.DTO.JobSeekerResponse;
import com.JobPortal.JobPortalBackend.Exception.UserNotFoundException;
import com.JobPortal.JobPortalBackend.Model.JobSeeker;
import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.JobSeekerProfileRepo;
import com.JobPortal.JobPortalBackend.SecurityService.AuthenticationService;
import com.JobPortal.JobPortalBackend.Services.JobSeekerProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobSeekerProfileServiceTest {

    @Mock
    private JobSeekerProfileRepo jobSeekerProfileRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private JobSeekerProfileService jobSeekerProfileService;

    private Users user;
    private JobSeeker profile;
    private UUID userId;
    private UUID profileId;

    @BeforeEach
    void setUp() {

        userId = UUID.randomUUID();
        profileId = UUID.randomUUID();

        user = new Users();
        user.setUserId(userId);
        user.setEmailId("test@gmail.com");

        profile = new JobSeeker();
        profile.setProfileId(profileId);
    }

    @Test
    void createProfile_ShouldSaveProfileSuccessfully() {

        when(jobSeekerProfileRepo.save(profile)).thenReturn(profile);

        jobSeekerProfileService.createProfile(user, profile);

        verify(jobSeekerProfileRepo).save(profile);

    }

    @Test
    void getMyProfile_ShouldReturnProfileResponse() {

        JobSeekerResponse response = new JobSeekerResponse();

        when(authenticationService.getLoggedInUser()).thenReturn(user);
        when(jobSeekerProfileRepo.findByUserUserId(userId)).thenReturn(Optional.of(profile));
        when(modelMapper.map(profile, JobSeekerResponse.class)).thenReturn(response);

        JobSeekerResponse result = jobSeekerProfileService.getMyProfile();

        assertNotNull(result);
        assertEquals(response, result);

        verify(authenticationService).getLoggedInUser();
        verify(jobSeekerProfileRepo).findByUserUserId(userId);
        verify(modelMapper).map(profile, JobSeekerResponse.class);
    }

    @Test
    void getMyProfile_ShouldThrowException_WhenProfileNotFound() {

        when(authenticationService.getLoggedInUser())
                .thenReturn(user);

        when(jobSeekerProfileRepo.findByUserUserId(userId))
                .thenReturn(Optional.empty());

        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class,
                        () -> jobSeekerProfileService.getMyProfile());

        assertEquals("Profile not found", exception.getMessage());

        verify(jobSeekerProfileRepo).findByUserUserId(userId);
    }

    @Test
    void getProfileById_ShouldReturnProfileResponse() {

        JobSeekerResponse response = new JobSeekerResponse();

        when(jobSeekerProfileRepo.findById(profileId))
                .thenReturn(Optional.of(profile));

        when(modelMapper.map(profile, JobSeekerResponse.class))
                .thenReturn(response);

        JobSeekerResponse result =
                jobSeekerProfileService.getProfileById(profileId);

        assertNotNull(result);
        assertEquals(response, result);

        verify(jobSeekerProfileRepo).findById(profileId);
        verify(modelMapper).map(profile, JobSeekerResponse.class);
    }

    @Test
    void getProfileById_ShouldThrowException_WhenProfileNotFound() {

        when(jobSeekerProfileRepo.findById(profileId))
                .thenReturn(Optional.empty());

        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class,
                        () -> jobSeekerProfileService.getProfileById(profileId));

        assertEquals("Profile not found", exception.getMessage());

        verify(jobSeekerProfileRepo).findById(profileId);
    }

    @Test
    void updateProfile_ShouldUpdateAndReturnResponse() {
        List<String> skis=List.of("Java","Advance Java","Spring Boot");
        List<String> edu=List.of("BE CS");
        List<String> exp=List.of("experience");

        JobSeekerRequest request = new JobSeekerRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhoneNumber("1234567890");
        request.setLocation("Pune");
        request.setSkills(skis);
        request.setEducation(edu);
        request.setExperience(exp);

        JobSeekerResponse response = new JobSeekerResponse();

        when(authenticationService.getLoggedInUser()).thenReturn(user);
        when(jobSeekerProfileRepo.findByUserUserId(userId)).thenReturn(Optional.of(profile));
        when(jobSeekerProfileRepo.save(any(JobSeeker.class))).thenReturn(profile);
        when(modelMapper.map(profile, JobSeekerResponse.class)).thenReturn(response);

        JobSeekerResponse result = jobSeekerProfileService.updateProfile(request);

        assertNotNull(result);

        assertEquals("John", profile.getFirstName());
        assertEquals("Doe", profile.getLastName());
        assertEquals("1234567890", profile.getPhoneNumber());
        assertEquals("Pune", profile.getLocation());
        assertEquals(skis, profile.getSkills());
        assertEquals(edu, profile.getEducation());
        assertEquals(exp, profile.getExperience());

        verify(jobSeekerProfileRepo).save(profile);
    }

    @Test
    void updateProfile_ShouldThrowException_WhenProfileNotFound() {

        JobSeekerRequest request = new JobSeekerRequest();

        when(authenticationService.getLoggedInUser())
                .thenReturn(user);

        when(jobSeekerProfileRepo.findByUserUserId(userId))
                .thenReturn(Optional.empty());

        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class,
                        () -> jobSeekerProfileService.updateProfile(request));

        assertEquals("Profile not found", exception.getMessage());

        verify(jobSeekerProfileRepo, never()).save(any());
    }
}
