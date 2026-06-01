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
import com.JobPortal.JobPortalBackend.SecurityLayer.AuthenticationService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
public class ApplicationService {

    private final JobPostRepo jobPostRepo;
    private final ApplicationRepo applicationRepo;
    private final AuthenticationService authenticationService;
    private final RecruiterProfileRepo recruiterProfileRepo;
    private final JobSeekerProfileRepo jobSeekerProfileRepo;
    private final EmailService emailService;

    public ApplicationService( JobPostRepo jobPostRepo, ApplicationRepo applicationRepo,
                                AuthenticationService authenticationService,RecruiterProfileRepo recruiterProfileRepo,
                               JobSeekerProfileRepo jobSeekerProfileRepo,EmailService emailService) {
        this.jobPostRepo = jobPostRepo;
        this.applicationRepo = applicationRepo;
        this.authenticationService=authenticationService;
        this.recruiterProfileRepo=recruiterProfileRepo;
        this.jobSeekerProfileRepo=jobSeekerProfileRepo;
        this.emailService=emailService;
    }

    private Users getCurrentUser() {
        return authenticationService.getLoggedInUser();
    }

    private JobSeeker getCurrentJobSeeker() {
        Users user = getCurrentUser();
        return jobSeekerProfileRepo.findByUserUserId(user.getUserId()).orElseThrow(() -> new UserNotFoundException("Profile not found"));
    }

    private Recruiter getCurrentRecruiter() {
        Users user = getCurrentUser();
        return recruiterProfileRepo.findByUserUserId(user.getUserId()).orElseThrow(() -> new UserNotFoundException("Profile not found"));
    }


    public Page<ApplicationDTO> getApplicantsList(UUID jobId , Pageable pageable) {

        Recruiter recruiter = getCurrentRecruiter();
        List<JobPost> jobPosts=recruiter.getJobPosts();

        boolean ownsJob = jobPosts.stream().anyMatch(job -> job.getJobId().equals(jobId));
        if(!ownsJob){
            throw  new AccessDeniedException("Job is not posted by you");
        }
        Page<JobApplication> applicationPage=applicationRepo.findAllByJobPostJobId(jobId,pageable);

        return applicationPage.map(application ->
                new ModelMapper().map(application, ApplicationDTO.class));
    }


    public ResponseEntity<String> applyForJob(UUID jobPostId) {
        JobPost jobPost=jobPostRepo.findById(jobPostId).orElseThrow(()-> new JobPostNotFound("Job Post not found: "+jobPostId));

        JobSeeker jobSeeker =getCurrentJobSeeker();
        List<JobApplication> appliedJobs=jobSeeker.getAppliedJobs();

        if(appliedJobs.stream().anyMatch(application -> application.getJobPost().getJobId().equals(jobPostId))){

            JobApplication existingJobApplication= appliedJobs.stream().filter(application->
                    application.getJobPost().getJobId().equals(jobPostId)).findFirst().orElseThrow(()->new ApplicationNotFoundException("Application not found"));


            return handleExistingApplication(existingJobApplication,jobSeeker);
        }

        JobApplication jobApplication=new JobApplication();
        jobApplication.setJobPost(jobPost);
        jobApplication.setJobSeeker(jobSeeker);
        jobApplication.setStatus(ApplicationStatus.APPLIED);
        jobApplication.setResume(jobSeeker.getResume());
        applicationRepo.save(jobApplication);

        return new ResponseEntity<>("Application submitted successfully", HttpStatus.OK);
    }


    public ResponseEntity<String> cancelApplication(UUID applicationId) {

        JobSeeker jobSeeker = getCurrentJobSeeker();
        List<JobApplication> applications=jobSeeker.getAppliedJobs();
        boolean belongsToUser=applications.stream().anyMatch(app->app.getApplicationId().equals(applicationId));
        if(!belongsToUser){
            return new ResponseEntity<>("Incorrect application Id", HttpStatus.BAD_REQUEST);
        }

        JobApplication jobApplication=applicationRepo.findById(applicationId).orElseThrow(()->new ApplicationNotFoundException("Application not found"));
        jobApplication.setStatus(ApplicationStatus.CANCELED);
        applicationRepo.save(jobApplication);


        return new ResponseEntity<>("Application canceled ", HttpStatus.OK);
    }

    public Page<ApplicationDTO> getMyApplications(Pageable pageable) {

        JobSeeker jobSeeker =getCurrentJobSeeker();
        Page<JobApplication>  jobApplications = applicationRepo.findAllByJobSeeker_ProfileId(jobSeeker.getProfileId(), pageable);

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

        JobApplication jobApplication=applicationRepo.findById(applicationId).orElseThrow(()-> new ApplicationNotFoundException("Application not found"));
        jobApplication.setStatus(ApplicationStatus.SHORTLISTED);
        applicationRepo.save(jobApplication);

        sendStatusEmail(jobApplication.getJobSeeker(),
                "Your application is shortlisted",
                "Dear applicant,\n\tCongratulations, your application is shortlisted");

        return new ResponseEntity<>("Shortlisted",HttpStatus.OK);
    }

    public ResponseEntity<?> rejectApplication(UUID applicationId) {

        JobApplication jobApplication=applicationRepo.findById(applicationId).orElseThrow(()-> new ApplicationNotFoundException("Application not found"));
        jobApplication.setStatus(ApplicationStatus.REJECTED);
        applicationRepo.save(jobApplication);

        sendStatusEmail(jobApplication.getJobSeeker(),
                "Your application is rejected",
                "Dear applicant,\n\tSorry, your application is rejected");
        return new ResponseEntity<>("Rejected",HttpStatus.OK);
    }

    private ResponseEntity<String> handleExistingApplication(JobApplication application, JobSeeker jobSeeker) {

        switch (application.getStatus()) {
            case CANCELED:
                application.setStatus(ApplicationStatus.APPLIED);
                application.setResume(jobSeeker.getResume());
                applicationRepo.save(application);
                return ResponseEntity.ok("Application submitted successfully");
            case REJECTED:
                return ResponseEntity.ok("Sorry, your were already applied for this job, got rejected");
            case SHORTLISTED:
                return ResponseEntity.ok("Congrats, your are shortlisted already, no action needed ");
            default:
                application.setResume(jobSeeker.getResume());
                applicationRepo.save(application);
                return ResponseEntity.badRequest().body("already applied for this job");
        }

    }
    private void sendStatusEmail(JobSeeker jobSeeker, String subject, String body) {

        emailService.sendEmail(jobSeeker.getEmailId(),subject, body);
    }
}
