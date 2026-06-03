package com.JobPortal.JobPortalBackend.Services;

import com.JobPortal.JobPortalBackend.DTO.ApplicationDTO;
import com.JobPortal.JobPortalBackend.Exception.ApplicationNotFoundException;
import com.JobPortal.JobPortalBackend.Exception.JobPostNotFound;
import com.JobPortal.JobPortalBackend.Exception.UserNotFoundException;
import com.JobPortal.JobPortalBackend.Model.*;
import com.JobPortal.JobPortalBackend.Repository.ApplicationRepo;
import com.JobPortal.JobPortalBackend.Repository.JobPostRepo;
import com.JobPortal.JobPortalBackend.Repository.JobSeekerProfileRepo;
import com.JobPortal.JobPortalBackend.Repository.RecruiterProfileRepo;
import com.JobPortal.JobPortalBackend.SecurityService.AuthenticationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Service
@Slf4j
@AllArgsConstructor
public class ApplicationService {

    private final JobPostRepo jobPostRepo;
    private final ApplicationRepo applicationRepo;
    private final AuthenticationService authenticationService;
    private final RecruiterProfileRepo recruiterProfileRepo;
    private final JobSeekerProfileRepo jobSeekerProfileRepo;
    private final EmailService emailService;
    private final ModelMapper modelMapper;


    private Users getCurrentUser() {
        return authenticationService.getLoggedInUser();
    }

    private JobSeeker getCurrentJobSeeker() {
        Users user = getCurrentUser();
        log.debug("Fetching JobSeeker profile for userId={}", user.getUserId());

        return jobSeekerProfileRepo.findByUserUserId(user.getUserId()).orElseThrow(() ->{
                log.warn("JobSeeker profile not found for userId={}", user.getUserId());
                return new UserNotFoundException("Profile not found");
            });
    }

    private Recruiter getCurrentRecruiter() {
        Users user = getCurrentUser();
        log.debug("Fetching Recruiter profile for userId={}", user.getUserId());

        return recruiterProfileRepo.findByUserUserId(user.getUserId()).orElseThrow(() ->{
                log.warn("Recruiter profile not found for userId={}", user.getUserId());
                return new UserNotFoundException("Profile not found");
            });
    }


    public Page<ApplicationDTO> getApplicantsList(UUID jobId , Pageable pageable) {

        log.info("Fetching applicants list for jobId={}", jobId);

        Recruiter recruiter = getCurrentRecruiter();

        boolean match=jobPostRepo.existsByJobIdAndRecruiterProfileId(jobId,recruiter.getProfileId());

        if(!match){
            log.warn("Recruiter profileId={} attempted to access applicants for unauthorized jobId={}", recruiter.getProfileId(), jobId);
            throw  new JobPostNotFound("Job is not posted by you");
        }
        Page<JobApplication> applicationPage=applicationRepo.findAllByJobPostJobId(jobId,pageable);
        log.info("Found {} applications for jobId={}", applicationPage.getTotalElements(), jobId);

        return applicationPage.map(application -> modelMapper.map(application, ApplicationDTO.class));
    }


    public ResponseEntity<String> applyForJob(UUID jobPostId) {
        log.info("Job application request received for jobPostId={}", jobPostId);

        JobPost jobPost=jobPostRepo.findById(jobPostId).orElseThrow(()->{
                log.warn("Job post not found for jobPostId={}", jobPostId);
                return new JobPostNotFound("Job Post not found: "+jobPostId);
            });

        JobSeeker jobSeeker =getCurrentJobSeeker();
        Optional<JobApplication> existingJobApplication = applicationRepo.findByJobPostJobIdAndJobSeekerProfileId(jobPostId,jobSeeker.getProfileId());

        if(existingJobApplication.isPresent()){
            log.warn("Duplicate application attempt by profileId={} for jobId={}", jobSeeker.getProfileId(), jobPostId);

            return handleExistingApplication(existingJobApplication.get(),jobSeeker);
        }

        JobApplication jobApplication=new JobApplication();
        jobApplication.setJobPost(jobPost);
        jobApplication.setJobSeeker(jobSeeker);
        jobApplication.setStatus(ApplicationStatus.APPLIED);
        jobApplication.setResume(jobSeeker.getResume());
        applicationRepo.save(jobApplication);

        log.info("Application submitted successfully. applicationId={}, profileId={}, jobId={}", jobApplication.getApplicationId(), jobSeeker.getProfileId(), jobPostId);

        return new ResponseEntity<>("Application submitted successfully", HttpStatus.OK);
    }


    public ResponseEntity<String> cancelApplication(UUID applicationId) {

        log.info("Cancel application request received for applicationId={}", applicationId);

        JobSeeker jobSeeker = getCurrentJobSeeker();
        Optional<JobApplication> jobApplication=applicationRepo.findByApplicationIdAndJobSeekerProfileId(applicationId,jobSeeker.getProfileId());

        if(jobApplication.isEmpty()){
            log.warn("Incorrect application Id / Unauthorized cancellation attempt by profileId={} for applicationId={}", jobSeeker.getProfileId(), applicationId);
            return new ResponseEntity<>("Incorrect application Id", HttpStatus.BAD_REQUEST);
        }
        JobApplication application=jobApplication.get();
        application.setStatus(ApplicationStatus.CANCELED);
        applicationRepo.save(application);
        log.info("Application canceled successfully. applicationId={}", applicationId);

        return new ResponseEntity<>("Application canceled ", HttpStatus.OK);
    }

    public Page<ApplicationDTO> getMyApplications(Pageable pageable) {

        JobSeeker jobSeeker =getCurrentJobSeeker();
        log.info("Fetching applications for profileId={}", jobSeeker.getProfileId());

        Page<JobApplication>  jobApplications = applicationRepo.findAllByJobSeekerProfileId(jobSeeker.getProfileId(), pageable);

        log.info("Found {} applications for profileId={}", jobApplications.getTotalElements(), jobSeeker.getProfileId());

        return jobApplications.map(application->new ApplicationDTO(
                application.getApplicationId(),
                application.getJobPost().getJobId(),
                application.getJobSeeker().getProfileId(),
                application.getStatus(),
                application.getResume()
                )
        );
    }

    @Transactional
    public ResponseEntity<?> shortlistApplication(UUID applicationId) {

        Recruiter recruiter=getCurrentRecruiter();

        Optional<JobApplication> jobApplication=applicationRepo.findByApplicationIdAndJobPostRecruiterProfileId(applicationId,recruiter.getProfileId());
        if(jobApplication.isEmpty()){

            log.warn("Application not found / Recruiter profileId={} attempted to access application for unauthorized applicationId={}", recruiter.getProfileId(), applicationId);
            throw new ApplicationNotFoundException("Application not found / Job is not posted by you");
        }

        log.info("Shortlisting applicationId={}", applicationId);
        JobApplication application=jobApplication.get();
        application.setStatus(ApplicationStatus.SHORTLISTED);
        applicationRepo.save(application);

        log.info("Application shortlisted successfully. applicationId={}", applicationId);

        sendStatusEmail(application.getJobSeeker(),
                "Your application is shortlisted",
                "Dear applicant,\n\tCongratulations, your application is shortlisted");

        return new ResponseEntity<>("Shortlisted",HttpStatus.OK);
    }

    public ResponseEntity<?> rejectApplication(UUID applicationId) {

        log.info("Rejecting applicationId={}", applicationId);

        Recruiter recruiter=getCurrentRecruiter();

        Optional<JobApplication> jobApplication=applicationRepo.findByApplicationIdAndJobPostRecruiterProfileId(applicationId,recruiter.getProfileId());

        if(jobApplication.isEmpty()){

            log.warn("Application not found / Recruiter profileId={} attempted to access application for unauthorized applicationId={}", recruiter.getProfileId(), applicationId);
            throw new ApplicationNotFoundException("Application not found / Job is not posted by you");
        }
        JobApplication application=jobApplication.get();
        application.setStatus(ApplicationStatus.REJECTED);
        applicationRepo.save(application);

        log.info("Application rejected successfully. applicationId={}", applicationId);

        sendStatusEmail(application.getJobSeeker(),
                "Your application is rejected",
                "Dear applicant,\n\tSorry, your application is rejected");
        return new ResponseEntity<>("Rejected",HttpStatus.OK);
    }

    private ResponseEntity<String> handleExistingApplication(JobApplication application, JobSeeker jobSeeker) {
        log.info("Handling existing applicationId={} with status={}", application.getApplicationId(), application.getStatus());

        switch (application.getStatus()) {
            case CANCELED:
                log.info("Re-applying canceled applicationId={}", application.getApplicationId());

                application.setStatus(ApplicationStatus.APPLIED);
                application.setResume(jobSeeker.getResume());
                applicationRepo.save(application);
                return ResponseEntity.ok("Application submitted successfully");
            case REJECTED:
                log.warn("Rejected application re-apply attempt. applicationId={}", application.getApplicationId());
                return ResponseEntity.ok("Sorry, your were already applied for this job, got rejected");
            case SHORTLISTED:
                log.info("Application already shortlisted. applicationId={}", application.getApplicationId());
                return ResponseEntity.ok("Congrats, your are shortlisted already, no action needed ");
            default:
                log.warn("Duplicate active application. applicationId={}", application.getApplicationId());
                application.setResume(jobSeeker.getResume());
                applicationRepo.save(application);
                return ResponseEntity.badRequest().body("already applied for this job");
        }

    }

    private void sendStatusEmail(JobSeeker jobSeeker, String subject, String body) {
        log.info("Sending email to {} with subject={}", jobSeeker.getEmailId(), subject);

        emailService.sendEmail(jobSeeker.getEmailId(),subject, body);

        log.info("Email sent successfully to {}", jobSeeker.getEmailId());
    }
}
