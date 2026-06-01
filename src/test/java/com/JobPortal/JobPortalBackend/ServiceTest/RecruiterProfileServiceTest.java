package com.JobPortal.JobPortalBackend.ServiceTest;

import com.JobPortal.JobPortalBackend.DTO.RecruiterRequest;
import com.JobPortal.JobPortalBackend.DTO.RecruiterResponse;
import com.JobPortal.JobPortalBackend.Exception.UserNotFoundException;
import com.JobPortal.JobPortalBackend.Model.Recruiter;
import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.RecruiterProfileRepo;
import com.JobPortal.JobPortalBackend.SecurityLayer.AuthenticationService;
import com.JobPortal.JobPortalBackend.Services.RecruiterProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecruiterProfileServiceTest {

    @Mock
    private RecruiterProfileRepo recruiterProfileRepo;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RecruiterProfileService recruiterProfileService;

    @Test
    void createProfile_ShouldSaveProfileSuccessfully() {

        Users user = new Users();
        user.setEmailId("test@gmail.com");

        Recruiter profile = new Recruiter();

        recruiterProfileService.createProfile(user, profile);

        assertNull(profile.getProfileId());
        assertEquals(user, profile.getUser());
        assertEquals("test@gmail.com", profile.getEmailId());
        assertEquals(profile, user.getRecruiter());

        verify(recruiterProfileRepo).save(profile);
    }

    @Test
    void getMyProfile_ShouldReturnRecruiterResponse() {

        UUID userId = UUID.randomUUID();

        Users user = new Users();
        user.setUserId(userId);

        Recruiter recruiter = new Recruiter();

        RecruiterResponse expectedResponse = new RecruiterResponse();

        when(authenticationService.getLoggedInUser()).thenReturn(user);
        when(recruiterProfileRepo.findByUserUserId(userId))
                .thenReturn(Optional.of(recruiter));
        when(modelMapper.map(recruiter, RecruiterResponse.class))
                .thenReturn(expectedResponse);

        RecruiterResponse result = recruiterProfileService.getMyProfile();

        assertNotNull(result);
        assertEquals(expectedResponse, result);

        verify(authenticationService).getLoggedInUser();
        verify(recruiterProfileRepo).findByUserUserId(userId);
        verify(modelMapper).map(recruiter, RecruiterResponse.class);
    }

    @Test
    void getMyProfile_ShouldThrowException_WhenProfileNotFound() {

        UUID userId = UUID.randomUUID();

        Users user = new Users();
        user.setUserId(userId);

        when(authenticationService.getLoggedInUser()).thenReturn(user);
        when(recruiterProfileRepo.findByUserUserId(userId))
                .thenReturn(Optional.empty());

        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class,
                        () -> recruiterProfileService.getMyProfile());

        assertEquals("Profile not found", exception.getMessage());

        verify(modelMapper, never())
                .map(any(), eq(RecruiterResponse.class));
    }

    @Test
    void getProfileByUserId_ShouldReturnProfile() {

        UUID profileId = UUID.randomUUID();

        Recruiter recruiter = new Recruiter();
        RecruiterResponse response = new RecruiterResponse();

        when(recruiterProfileRepo.findById(profileId))
                .thenReturn(Optional.of(recruiter));

        when(modelMapper.map(recruiter, RecruiterResponse.class))
                .thenReturn(response);

        RecruiterResponse result =
                recruiterProfileService.getProfileByUserId(profileId);

        assertNotNull(result);
        assertEquals(response, result);

        verify(recruiterProfileRepo).findById(profileId);
        verify(modelMapper).map(recruiter, RecruiterResponse.class);
    }

    @Test
    void getProfileByUserId_ShouldThrowException_WhenProfileNotFound() {

        UUID profileId = UUID.randomUUID();

        when(recruiterProfileRepo.findById(profileId))
                .thenReturn(Optional.empty());

        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class,
                        () -> recruiterProfileService.getProfileByUserId(profileId));

        assertEquals("Profile not found", exception.getMessage());

        verify(modelMapper, never())
                .map(any(), eq(RecruiterResponse.class));
    }

    @Test
    void updateProfile_ShouldUpdateSuccessfully() {

        UUID userId = UUID.randomUUID();

        Users user = new Users();
        user.setUserId(userId);

        Recruiter existingProfile = new Recruiter();

        RecruiterRequest request = new RecruiterRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setCompanyName("Google");
        request.setIndustryType("IT");

        RecruiterResponse response = new RecruiterResponse();

        when(authenticationService.getLoggedInUser()).thenReturn(user);

        when(recruiterProfileRepo.findByUserUserId(userId))
                .thenReturn(Optional.of(existingProfile));

        when(recruiterProfileRepo.save(existingProfile))
                .thenReturn(existingProfile);

        when(modelMapper.map(existingProfile, RecruiterResponse.class))
                .thenReturn(response);

        RecruiterResponse result =
                recruiterProfileService.updateProfile(request);

        assertNotNull(result);
        assertEquals(response, result);

        assertEquals("John", existingProfile.getFirstName());
        assertEquals("Doe", existingProfile.getLastName());
        assertEquals("Google", existingProfile.getCompanyName());
        assertEquals("IT", existingProfile.getIndustryType());

        verify(recruiterProfileRepo).save(existingProfile);
    }

    @Test
    void updateProfile_ShouldThrowException_WhenProfileNotFound() {

        UUID userId = UUID.randomUUID();

        Users user = new Users();
        user.setUserId(userId);

        RecruiterRequest request = new RecruiterRequest();

        when(authenticationService.getLoggedInUser()).thenReturn(user);

        when(recruiterProfileRepo.findByUserUserId(userId))
                .thenReturn(Optional.empty());

        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class,
                        () -> recruiterProfileService.updateProfile(request));

        assertEquals("Profile not found", exception.getMessage());

        verify(recruiterProfileRepo, never()).save(any());
        verify(modelMapper, never())
                .map(any(), eq(RecruiterResponse.class));
    }
}