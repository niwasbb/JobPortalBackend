package com.JobPortal.JobPortalBackend.Services;

import com.JobPortal.JobPortalBackend.DTO.ApplicationDTO;
import com.JobPortal.JobPortalBackend.Exception.JobPostNotFound;
import com.JobPortal.JobPortalBackend.Model.ApplicationStatus;
import com.JobPortal.JobPortalBackend.Model.JobApplication;
import com.JobPortal.JobPortalBackend.Model.JobPost;
import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.ApplicationRepo;
import com.JobPortal.JobPortalBackend.Repository.JobPostRepo;
import com.JobPortal.JobPortalBackend.Repository.UserRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
public class ApplicationService {

    private final JobPostRepo jobPostRepo;
    private final ApplicationRepo applicationRepo;
    private final UserRepo userRepo;


    public ApplicationService( JobPostRepo jobPostRepo, ApplicationRepo applicationRepo, UserRepo userRepo) {
        this.jobPostRepo = jobPostRepo;
        this.applicationRepo = applicationRepo;
        this.userRepo = userRepo;
    }


    public Users getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = auth.getName();
        return userRepo.findByUsername(loggedInUsername).orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Authentication problem"));
    }


    public Page<ApplicationDTO> getApplicantsList(String jobId ,Pageable pageable) {
        Users user=this.getLoggedInUser();
        if(user.getRecruiterProfile().getJobPosts().stream().noneMatch(jobPost -> jobPost.getJobId().equals(jobId))){
            throw new AuthorizationServiceException("You are not authorized to view the applicants for this job");
        }
        Page<JobApplication> applicationPage=applicationRepo.findAllByJobId(jobId,pageable);
        return applicationPage.map(application -> new ApplicationDTO(application.getApplicationId(),
                application.getJobPost().getJobId(),
                application.getJobSeekerProfile().getProfileId(),
                application.getStatus()
                ));
    }


    public ResponseEntity<String> applyForJob(String jobPostId) {
        Users user=this.getLoggedInUser();
        JobPost jobPost=jobPostRepo.findById(jobPostId).orElseThrow(()-> new JobPostNotFound("Job Post not found with id: "+jobPostId));
        if(user.getJobSeekerProfile().getAppliedJobs().stream().anyMatch(application -> application.getJobPost().getJobId().equals(jobPostId))){
            return new ResponseEntity<>("You have already applied for this job", HttpStatus.BAD_REQUEST);
        }
        JobApplication jobApplication=new JobApplication();
        jobApplication.setJobPost(jobPost);
        jobApplication.setJobSeekerProfile(user.getJobSeekerProfile());
        jobApplication.setStatus(ApplicationStatus.APPLIED);
        applicationRepo.save(jobApplication);
        return new ResponseEntity<>("Application submitted successfully", HttpStatus.OK);
    }


    public ResponseEntity<String> deleteApplication(String applicationId) {
        Users user=this.getLoggedInUser();
        if(user.getJobSeekerProfile().getAppliedJobs().stream().noneMatch(application -> application.getApplicationId().equals(applicationId))){
            return new ResponseEntity<>("You have not applied for this job", HttpStatus.BAD_REQUEST);
        }
        applicationRepo.deleteById(applicationId);
        return new ResponseEntity<>("Application deleted successfully", HttpStatus.OK);
    }

    public Page<ApplicationDTO> getMyApplications(Pageable pageable) {
        Users user=this.getLoggedInUser();
        Page<JobApplication>  jobApplications = applicationRepo.findAllByJobSeekerProfile_ProfileId(user.getJobSeekerProfile().getProfileId(), pageable);
        return jobApplications.map(application->new ApplicationDTO(application.getApplicationId(),
                application.getJobPost().getJobId(),
                application.getJobSeekerProfile().getProfileId(),
                application.getStatus())
                );
    }
}
