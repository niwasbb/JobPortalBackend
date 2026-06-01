package com.JobPortal.JobPortalBackend.ServiceTest;


import com.JobPortal.JobPortalBackend.Exception.ApplicationNotFoundException;
import com.JobPortal.JobPortalBackend.Exception.JobPostNotFound;
import com.JobPortal.JobPortalBackend.Model.*;
import com.JobPortal.JobPortalBackend.Repository.ApplicationRepo;
import com.JobPortal.JobPortalBackend.Repository.JobPostRepo;
import com.JobPortal.JobPortalBackend.Repository.JobSeekerProfileRepo;
import com.JobPortal.JobPortalBackend.Repository.RecruiterProfileRepo;
import com.JobPortal.JobPortalBackend.SecurityLayer.AuthenticationService;
import com.JobPortal.JobPortalBackend.Services.ApplicationService;
import com.JobPortal.JobPortalBackend.Services.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private JobPostRepo jobPostRepo;

    @Mock
    private ApplicationRepo applicationRepo;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private RecruiterProfileRepo recruiterProfileRepo;

    @Mock
    private JobSeekerProfileRepo jobSeekerProfileRepo;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ApplicationService applicationService;

    private Users createUser() {
        Users user = new Users();
        user.setUserId(UUID.randomUUID());
        return user;
    }

    private JobSeeker createJobSeeker() {

        JobSeeker js = new JobSeeker();
        js.setProfileId(UUID.randomUUID());
        js.setResume("resume.pdf");
        js.setEmailId("test@gmail.com");
        js.setAppliedJobs(new ArrayList<>());
        return js;
    }
    @Test
    void applyForJob_ShouldCreateNewApplication() {

        UUID jobId = UUID.randomUUID();

        Users user = createUser();
        JobSeeker seeker = createJobSeeker();

        JobPost post = new JobPost();
        post.setJobId(jobId);

        when(jobPostRepo.findById(jobId)).thenReturn(Optional.of(post));
        when(authenticationService.getLoggedInUser()).thenReturn(user);
        when(jobSeekerProfileRepo.findByUserUserId(user.getUserId())).thenReturn(Optional.of(seeker));

        ResponseEntity<String> response = applicationService.applyForJob(jobId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Application submitted successfully", response.getBody());

        verify(applicationRepo).save(any(JobApplication.class));
    }
    @Test
    void applyForJob_ShouldThrow_WhenJobNotFound() {

        UUID jobId = UUID.randomUUID();

        when(jobPostRepo.findById(jobId)).thenReturn(Optional.empty());

        assertThrows(JobPostNotFound.class, () -> applicationService.applyForJob(jobId));
    }
    @Test
    void applyForJob_ShouldReapply_WhenCancelled() {

        UUID jobId = UUID.randomUUID();

        Users user = createUser();
        JobSeeker seeker = createJobSeeker();

        JobPost post = new JobPost();
        post.setJobId(jobId);

        JobApplication application = new JobApplication();
        application.setJobPost(post);
        application.setStatus(ApplicationStatus.CANCELED);

        seeker.getAppliedJobs().add(application);

        when(jobPostRepo.findById(jobId)).thenReturn(Optional.of(post));
        when(authenticationService.getLoggedInUser()).thenReturn(user);
        when(jobSeekerProfileRepo.findByUserUserId(user.getUserId())).thenReturn(Optional.of(seeker));

        ResponseEntity<String> response = applicationService.applyForJob(jobId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(applicationRepo).save(application);

        assertEquals(ApplicationStatus.APPLIED, application.getStatus());
    }

    @Test
    void applyForJob_ShouldReturnRejectedMessage() {

        UUID jobId = UUID.randomUUID();

        Users user = createUser();
        JobSeeker seeker = createJobSeeker();

        JobPost post = new JobPost();
        post.setJobId(jobId);

        JobApplication application = new JobApplication();
        application.setJobPost(post);
        application.setStatus(ApplicationStatus.REJECTED);

        seeker.getAppliedJobs().add(application);

        when(jobPostRepo.findById(jobId)).thenReturn(Optional.of(post));
        when(authenticationService.getLoggedInUser()).thenReturn(user);
        when(jobSeekerProfileRepo.findByUserUserId(user.getUserId())).thenReturn(Optional.of(seeker));

        ResponseEntity<String> response = applicationService.applyForJob(jobId);

        assertTrue(response.getBody().contains("rejected"));
        verify(applicationRepo, never()).save(any());
    }

    @Test
    void applyForJob_ShouldReturnShortlistedMessage() {

        UUID jobId = UUID.randomUUID();

        Users user = createUser();
        JobSeeker seeker = createJobSeeker();

        JobPost post =new JobPost();
        post.setJobId(jobId);
        JobApplication application = new JobApplication();
        application.setJobPost(post);
        application.setStatus(ApplicationStatus.SHORTLISTED);

        seeker.getAppliedJobs().add(application);

        when(jobPostRepo.findById(jobId)).thenReturn(Optional.of(post));
        when(authenticationService.getLoggedInUser()).thenReturn(user);
        when(jobSeekerProfileRepo.findByUserUserId(user.getUserId())).thenReturn(Optional.of(seeker));

        ResponseEntity<String> response = applicationService.applyForJob(jobId);

        assertTrue(response.getBody().contains("shortlisted"));
    }

    @Test
    void applyForJob_ShouldReturnBadRequest_WhenAlreadyApplied() {

        UUID jobId = UUID.randomUUID();

        Users user = createUser();
        JobSeeker seeker = createJobSeeker();

        JobPost post = new JobPost();
        post.setJobId(jobId);

        JobApplication application = new JobApplication();
        application.setJobPost(post);
        application.setStatus(ApplicationStatus.APPLIED);

        seeker.getAppliedJobs().add(application);

        when(jobPostRepo.findById(jobId)).thenReturn(Optional.of(post));
        when(authenticationService.getLoggedInUser()).thenReturn(user);
        when(jobSeekerProfileRepo.findByUserUserId(user.getUserId())).thenReturn(Optional.of(seeker));

        ResponseEntity<String> response = applicationService.applyForJob(jobId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    void cancelApplication_ShouldCancelSuccessfully() {

        UUID appId = UUID.randomUUID();

        Users user = createUser();
        JobSeeker seeker = createJobSeeker();

        JobApplication app = new JobApplication();
        app.setApplicationId(appId);

        seeker.getAppliedJobs().add(app);

        when(authenticationService.getLoggedInUser()).thenReturn(user);
        when(jobSeekerProfileRepo.findByUserUserId(user.getUserId())).thenReturn(Optional.of(seeker));
        when(applicationRepo.findById(appId)).thenReturn(Optional.of(app));

        ResponseEntity<String> response = applicationService.cancelApplication(appId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ApplicationStatus.CANCELED, app.getStatus());
        verify(applicationRepo).save(app);
    }

    @Test
    void cancelApplication_ShouldReturnBadRequest_WhenNotOwned() {

        UUID appId = UUID.randomUUID();

        Users user = createUser();
        JobSeeker seeker = createJobSeeker();

        when(authenticationService.getLoggedInUser()).thenReturn(user);
        when(jobSeekerProfileRepo.findByUserUserId(user.getUserId())).thenReturn(Optional.of(seeker));

        ResponseEntity<String> response = applicationService.cancelApplication(appId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shortlistApplication_ShouldUpdateStatusAndSendMail() {

        UUID appId = UUID.randomUUID();

        JobSeeker seeker = createJobSeeker();

        JobApplication application = new JobApplication();
        application.setJobSeeker(seeker);

        when(applicationRepo.findById(appId)).thenReturn(Optional.of(application));

        ResponseEntity<?> response = applicationService.shortlistApplication(appId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ApplicationStatus.SHORTLISTED, application.getStatus());

        verify(applicationRepo).save(application);
        verify(emailService).sendEmail(eq(seeker.getEmailId()), contains("shortlisted"), anyString());
    }
    @Test
    void rejectApplication_ShouldUpdateStatusAndSendMail() {

        UUID appId = UUID.randomUUID();

        JobSeeker seeker = createJobSeeker();

        JobApplication application = new JobApplication();
        application.setJobSeeker(seeker);

        when(applicationRepo.findById(appId)).thenReturn(Optional.of(application));

        ResponseEntity<?> response = applicationService.rejectApplication(appId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ApplicationStatus.REJECTED, application.getStatus());
        verify(applicationRepo).save(application);
        verify(emailService).sendEmail(eq(seeker.getEmailId()), contains("rejected"), anyString());
    }
    @Test
    void shortlistApplication_ShouldThrow_WhenApplicationNotFound() {

        UUID appId = UUID.randomUUID();

        when(applicationRepo.findById(appId)).thenReturn(Optional.empty());
        assertThrows(ApplicationNotFoundException.class, () -> applicationService.shortlistApplication(appId));
    }

    @Test
    void rejectApplication_ShouldThrow_WhenApplicationNotFound() {

        UUID appId = UUID.randomUUID();

        when(applicationRepo.findById(appId)).thenReturn(Optional.empty());

        assertThrows(ApplicationNotFoundException.class, () -> applicationService.rejectApplication(appId));
    }
}