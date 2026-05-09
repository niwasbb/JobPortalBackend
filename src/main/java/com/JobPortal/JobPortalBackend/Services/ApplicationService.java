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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class ApplicationService {

    private final JobPostRepo jobPostRepo;
    private final ApplicationRepo applicationRepo;
    private final AuthenticationService authenticationService;
    private final RecruiterProfileRepo recruiterProfileRepo;
    private final JobSeekerProfileRepo jobSeekerProfileRepo;

    @Autowired
    public ApplicationService( JobPostRepo jobPostRepo, ApplicationRepo applicationRepo,
                                AuthenticationService authenticationService,RecruiterProfileRepo recruiterProfileRepo,
                               JobSeekerProfileRepo jobSeekerProfileRepo) {
        this.jobPostRepo = jobPostRepo;
        this.applicationRepo = applicationRepo;
        this.authenticationService=authenticationService;
        this.recruiterProfileRepo=recruiterProfileRepo;
        this.jobSeekerProfileRepo=jobSeekerProfileRepo;
    }



    public Page<ApplicationDTO> getApplicantsList(UUID jobId , Pageable pageable) {

        Users user=authenticationService.getLoggedInUser();
        Recruiter recruiter =recruiterProfileRepo.findByUserUserId(user.getUserId()).orElseThrow(()-> new UserNotFoundException("Profile not found"));
        List<JobPost> jobPosts=jobPostRepo.findAllByRecruiter_ProfileId(recruiter.getProfileId());

        if(jobPosts.stream().noneMatch(jobPost -> jobPost.getJobId().equals(jobId))){
            throw  new AccessDeniedException("Job is not posted by you");
        }

        Page<JobApplication> applicationPage=applicationRepo.findAllByJobPostJobId(jobId,pageable);

        return applicationPage.map(application ->
                new ModelMapper().map(application, ApplicationDTO.class));
    }


    public ResponseEntity<String> applyForJob(UUID jobPostId) {
        JobPost jobPost=jobPostRepo.findById(jobPostId).orElseThrow(()-> new JobPostNotFound("Job Post not found: "+jobPostId));

        Users user=authenticationService.getLoggedInUser();
        JobSeeker jobSeeker =jobSeekerProfileRepo.findByUserUserId(user.getUserId()).orElseThrow(()-> new UserNotFoundException("profile not found"));
        List<JobApplication> appliedJobs=applicationRepo.findAllByJobSeeker_ProfileId(jobSeeker.getProfileId());

        if(appliedJobs.stream().anyMatch(application -> application.getJobPost().getJobId().equals(jobPostId))){

            JobApplication jobApplication= appliedJobs.stream().filter(application->
                    application.getJobPost().getJobId().equals(jobPostId)).findFirst().orElseThrow(()->new ApplicationNotFoundException("Application not found"));

            if(jobApplication.getStatus()==ApplicationStatus.CANCELED){

                jobApplication.setStatus(ApplicationStatus.APPLIED);
                jobApplication.setResume(jobSeeker.getResume());
                applicationRepo.save(jobApplication);
                return new ResponseEntity<>("Application submitted successfully",HttpStatus.OK);

            }
            if(jobApplication.getStatus()==ApplicationStatus.REJECTED){

                return new ResponseEntity<>("Sorry, your were already applied for this job, got rejected",HttpStatus.OK);
            }
            if(jobApplication.getStatus()==ApplicationStatus.SHORTLISTED){

                return new ResponseEntity<>("Congrats, your are shortlisted already, no action needed ",HttpStatus.OK);
            }
            jobApplication.setResume(jobSeeker.getResume());
            applicationRepo.save(jobApplication);
            return new ResponseEntity<>("already applied for this job", HttpStatus.BAD_REQUEST);
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

        Users user=authenticationService.getLoggedInUser();
        JobSeeker jobSeeker =jobSeekerProfileRepo.findByUserUserId(user.getUserId()).orElseThrow(()-> new UserNotFoundException("profile not found"));
        List<JobApplication> appliedJobs=applicationRepo.findAllByJobSeeker_ProfileId(jobSeeker.getProfileId());

        if(appliedJobs.stream().noneMatch(application -> application.getApplicationId().equals(applicationId))){
            return new ResponseEntity<>("Incorrect application Id", HttpStatus.BAD_REQUEST);
        }

        JobApplication jobApplication=applicationRepo.findById(applicationId).orElseThrow(()->new ApplicationNotFoundException("Application not found"));
        jobApplication.setStatus(ApplicationStatus.CANCELED);
        applicationRepo.save(jobApplication);


        return new ResponseEntity<>("Application canceled ", HttpStatus.OK);
    }

    public Page<ApplicationDTO> getMyApplications(Pageable pageable) {

        Users user=authenticationService.getLoggedInUser();
        JobSeeker jobSeeker =jobSeekerProfileRepo.findByUserUserId(user.getUserId()).orElseThrow(()-> new UserNotFoundException("profile not found"));
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

    public ResponseEntity<?> shortlistApplication(UUID applicationId) {

        JobApplication jobApplication=applicationRepo.findById(applicationId).orElseThrow(()-> new ApplicationNotFoundException("Application not found"));
        jobApplication.setStatus(ApplicationStatus.SHORTLISTED);
        applicationRepo.save(jobApplication);

        return new ResponseEntity<>("Shortlisted",HttpStatus.OK);
    }

    public ResponseEntity<?> rejectApplication(UUID applicationId) {

        JobApplication jobApplication=applicationRepo.findById(applicationId).orElseThrow(()-> new ApplicationNotFoundException("Application not found"));
        jobApplication.setStatus(ApplicationStatus.REJECTED);
        applicationRepo.save(jobApplication);

        return new ResponseEntity<>("Rejected",HttpStatus.OK);
    }
}
