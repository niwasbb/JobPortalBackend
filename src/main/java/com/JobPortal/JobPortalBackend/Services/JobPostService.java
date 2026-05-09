package com.JobPortal.JobPortalBackend.Services;

import com.JobPortal.JobPortalBackend.DTO.JobPostRequest;
import com.JobPortal.JobPortalBackend.DTO.JobPostResponse;
import com.JobPortal.JobPortalBackend.Exception.JobPostNotFound;
import com.JobPortal.JobPortalBackend.Exception.UserNotFoundException;
import com.JobPortal.JobPortalBackend.Model.JobPost;
import com.JobPortal.JobPortalBackend.Model.Recruiter;
import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.JobPostRepo;
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
public class JobPostService {

    private final JobPostRepo jobPostRepo;
    private final ModelMapper modelMapper;
    private final AuthenticationService authenticationService;
    private final RecruiterProfileRepo recruiterProfileRepo;


    @Autowired
    public JobPostService(JobPostRepo jobPostRepo, ModelMapper modelMapper,
                          AuthenticationService authenticationService,RecruiterProfileRepo recruiterProfileRepo) {

        this.jobPostRepo=jobPostRepo;
        this.modelMapper=modelMapper;
        this.authenticationService=authenticationService;
        this.recruiterProfileRepo=recruiterProfileRepo;
    }



    public Page<JobPostResponse> getJobs(String searchBy, Pageable pageable){
        Page<JobPost> jobPostPage;

        if(searchBy ==null|| searchBy.isEmpty()){
            jobPostPage =jobPostRepo.findAll(pageable);
        }
        else
            jobPostPage = jobPostRepo.findAll(searchBy,pageable);

        return jobPostPage.map(jobPost ->
            new JobPostResponse(jobPost.getJobId(),
                    jobPost.getTitle(),
                    jobPost.getPostedDate(),
                    jobPost.getRecruiter().getCompanyName(),
                    jobPost.getLocation(),
                    jobPost.getJobDescription(),
                    jobPost.getRequiredSkills(),
                    jobPost.getRequiredEducation(),
                    jobPost.getNoOfVacancy(),
                    jobPost.getSalaryRange())
        );
    }



    public JobPostResponse postNewJob(JobPostRequest newJobPost) {

        Users user= authenticationService.getLoggedInUser();
        Recruiter recruiter =recruiterProfileRepo.findByUserUserId(user.getUserId()).orElseThrow(()-> new UserNotFoundException("Recruiter Profile not found for user "+user.getUsername()));

        JobPost jobPost=modelMapper.map(newJobPost,JobPost.class);
        jobPost.setRecruiter(recruiter);

        JobPost savedJobPost= jobPostRepo.save(jobPost);
        return modelMapper.map(savedJobPost, JobPostResponse.class);
    }

    public JobPostResponse getJobPostById(UUID jobPostId){

        JobPost jobPost=jobPostRepo.findById(jobPostId).orElseThrow(()-> new JobPostNotFound("Job Post not found "+jobPostId));

        return modelMapper.map(jobPost, JobPostResponse.class);
    }


    public ResponseEntity<String> deleteJobPost(UUID jobPostId) {

        JobPost jobPost=jobPostRepo.findById(jobPostId).orElseThrow(()-> new JobPostNotFound("Job Post not found with"));
        Users user= authenticationService.getLoggedInUser();
        Recruiter recruiter =recruiterProfileRepo.findByUserUserId(user.getUserId()).orElseThrow(()-> new UserNotFoundException("Profile not found "+user.getUsername()));
        List<JobPost> jobPosts=jobPostRepo.findAllByRecruiter_ProfileId(recruiter.getProfileId());

        if(jobPosts.stream().noneMatch(jobPost1 -> jobPost1.equals(jobPost))){

            throw new AccessDeniedException(" Job is not posted by you");
        }
        jobPostRepo.deleteById(jobPostId);

        return new ResponseEntity<>("Job Post deleted successfully", HttpStatus.OK);
    }

    public JobPostResponse updateJobPost(UUID jobPostId, JobPostRequest updatedJobPost) {
        JobPost jobPost=jobPostRepo.findById(jobPostId).orElseThrow(()-> new JobPostNotFound("Job Post not found"));
        Users user= authenticationService.getLoggedInUser();
        Recruiter recruiter =recruiterProfileRepo.findByUserUserId(user.getUserId()).orElseThrow(()-> new UserNotFoundException("Profile not found "+user.getUsername()));
        List<JobPost> jobPosts=jobPostRepo.findAllByRecruiter_ProfileId(recruiter.getProfileId());

        if(jobPosts.stream().noneMatch(jobPost1 -> jobPost1.equals(jobPost))){

            throw new AccessDeniedException(" Job is not posted by you");
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

        return modelMapper.map(jobPost, JobPostResponse.class);
    }
}
