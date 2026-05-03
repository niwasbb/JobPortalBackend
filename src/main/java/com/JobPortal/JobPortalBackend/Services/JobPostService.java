package com.JobPortal.JobPortalBackend.Services;

import com.JobPortal.JobPortalBackend.DTO.JobPostDTO;
import com.JobPortal.JobPortalBackend.Exception.JobPostNotFound;
import com.JobPortal.JobPortalBackend.Model.*;
import com.JobPortal.JobPortalBackend.Repository.JobPostRepo;
import com.JobPortal.JobPortalBackend.Repository.UserRepo;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class JobPostService {

    private final JobPostRepo jobPostRepo;
    private final ModelMapper modelMapper;
    private final UserRepo  userRepo;


    @Autowired
    public JobPostService(JobPostRepo jobPostRepo, ModelMapper modelMapper, UserRepo userRepo) {

        this.jobPostRepo=jobPostRepo;
        this.modelMapper=modelMapper;
        this.userRepo=userRepo;
    }



    public Page<JobPostDTO> getJobs(String searchBy, Pageable pageable){
        Page<JobPost> jobPostPage;

        if(searchBy ==null|| searchBy.isEmpty()){
            jobPostPage =jobPostRepo.findAll(pageable);
        }
        else
            jobPostPage = jobPostRepo.findAll(searchBy,pageable);

        return jobPostPage.map(jobPost ->
            new JobPostDTO(jobPost.getJobId(),
                    jobPost.getTitle(),
                    jobPost.getPostedDate(),
                    jobPost.getRecruiterProfile().getCompanyName(),
                    jobPost.getLocation(),
                    jobPost.getJobDescription(),
                    jobPost.getRequiredSkills(),
                    jobPost.getRequiredEducation(),
                    jobPost.getNoOfVacancy(),
                    jobPost.getSalaryRange())
        );
    }

    public Users getLoggedinUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = auth.getName();

        return userRepo.findByUsername(loggedInUsername).orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Authentication problem"));

    }

//    public JobPostDTO getJobPostById(String jobId) {
//
//        JobPost jobPost= jobPostRepo.findById(jobId).orElseThrow(()-> new JobPostNotFound("Job Post not found with id: "+ jobId));
//        return modelMapper.map(jobPost,JobPostDTO.class);
//
//    }

    public JobPostDTO postNewJob(JobPostDTO newJobPost) {

        Users user= this.getLoggedinUser();

        JobPost jobPost=modelMapper.map(newJobPost,JobPost.class);
        jobPost.setRecruiterProfile(user.getRecruiterProfile());

        JobPost savedJobPost= jobPostRepo.save(jobPost);
        return modelMapper.map(savedJobPost,JobPostDTO.class);
    }


    public ResponseEntity<String> deleteJobPost(String jobPostId) {

        JobPost jobPost=jobPostRepo.findById(jobPostId).orElseThrow(()-> new JobPostNotFound("Job Post not found with" ));

        Users user= this.getLoggedinUser();

        if(!user.getRecruiterProfile().getProfileId().equals(jobPost.getRecruiterProfile().getProfileId())){

            throw new AuthorizationServiceException(HttpStatus.UNAUTHORIZED+" You are not Authorized to delete this post");
        }
        jobPostRepo.deleteById(jobPostId);

        return new ResponseEntity<>("Job Post deleted successfully", HttpStatus.OK);
    }


    public JobPostDTO updateJobPost(String jobPostId, @Valid JobPostDTO updatedJobPost) {
        JobPost jobPost=jobPostRepo.findById(jobPostId).orElseThrow(()-> new JobPostNotFound("Job Post not found"));
        Users user= this.getLoggedinUser();

        if(!user.getRecruiterProfile().getProfileId().equals(jobPost.getRecruiterProfile().getProfileId())){

            throw new AuthorizationServiceException(HttpStatus.UNAUTHORIZED+" You are not Authorized to update this post");
        }
        jobPost.setJobDescription(updatedJobPost.getJobDescription());
        jobPost.setTitle(updatedJobPost.getTitle());
        jobPost.setLocation(updatedJobPost.getLocation());
        jobPost.setNoOfVacancy(updatedJobPost.getNoOfVacancy());
        jobPost.setRequiredEducation(updatedJobPost.getRequiredEducation());
        jobPost.setRequiredSkills(updatedJobPost.getRequiredSkills());
        jobPost.setSalaryRange(updatedJobPost.getSalaryRange());
        jobPost.setCompanyName(updatedJobPost.getCompanyName());

        jobPostRepo.save(jobPost);

        return modelMapper.map(jobPost,JobPostDTO.class);
    }
}
