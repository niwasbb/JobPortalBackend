package com.JobPortal.JobPortalBackend.Services;

import com.JobPortal.JobPortalBackend.DTO.JobPostRequest;
import com.JobPortal.JobPortalBackend.DTO.JobPostResponse;
import com.JobPortal.JobPortalBackend.Exception.JobPostNotFound;
import com.JobPortal.JobPortalBackend.Model.JobPost;
import com.JobPortal.JobPortalBackend.Model.Recruiter;
import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.JobPostRepo;
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

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class JobPostService {

    private final JobPostRepo jobPostRepo;
    private final ModelMapper modelMapper;
    private final AuthenticationService authenticationService;
    private final RecruiterProfileRepo recruiterProfileRepo;


    public Page<JobPostResponse> getJobs(String searchBy, Pageable pageable){
        log.info("Fetching job posts. SearchBy: {}, Page: {}, Size: {}", searchBy, pageable.getPageNumber(), pageable.getPageSize());

        Page<JobPost> jobPostPage;

        if(searchBy ==null|| searchBy.isEmpty()){
            log.debug("Fetching all job posts");
            jobPostPage =jobPostRepo.findAll(pageable);
        }
        else {
            log.debug("Searching job posts with keyword: {}", searchBy);
            jobPostPage = jobPostRepo.searchByKeyword(searchBy, pageable);
        }

        log.info("Retrieved {} job posts", jobPostPage.getNumberOfElements());

        return jobPostPage.map(jobPost ->
            new JobPostResponse(jobPost.getJobId(),
                    jobPost.getTitle(),
                    jobPost.getPostedDate(),
                    jobPost.getRecruiter().getCompanyName(),
                    jobPost.getLocation(),
                    jobPost.getJobDescription(),
                    jobPost.getRequiredSkills(),
                    jobPost.getRequiredEducation(),
                    jobPost.getRequiredExperience(),
                    jobPost.getNoOfVacancy(),
                    jobPost.getSalaryRange())
        );
    }

    private Recruiter getRecruiter(){
        Users user=authenticationService.getLoggedInUser();
        return user.getRecruiter();
    }

    public JobPostResponse postNewJob(JobPostRequest newJobPost) {

        Recruiter recruiter =getRecruiter();

        log.info("RecruiterId {} is creating a new job post", recruiter.getProfileId());

        JobPost jobPost=modelMapper.map(newJobPost,JobPost.class);
        jobPost.setRecruiter(recruiter);

        JobPost savedJobPost= jobPostRepo.save(jobPost);

        log.info("Job post created successfully. JobId: {}, RecruiterId: {}", savedJobPost.getJobId(), recruiter.getProfileId());

        return modelMapper.map(savedJobPost, JobPostResponse.class);
    }

    public JobPostResponse getJobPostById(UUID jobPostId){
        log.info("Fetching job post with ID: {}", jobPostId);

        JobPost jobPost=jobPostRepo.findById(jobPostId).orElseThrow(()->{
                log.warn("Job post not found. JobId: {}", jobPostId);
                return new JobPostNotFound("Job Post not found "+jobPostId);
            });

        log.info("Job post found. JobId: {}", jobPostId);

        return modelMapper.map(jobPost, JobPostResponse.class);
    }

    public ResponseEntity<String> deleteJobPost(UUID jobPostId) {

        log.info("Delete request received for JobId: {}", jobPostId);

        Recruiter recruiter =getRecruiter();
        boolean match=jobPostRepo.existsByJobIdAndRecruiterProfileId(jobPostId,recruiter.getProfileId());
        if(!match){
            log.warn("JobPost not found / Unauthorized delete attempt. RecruiterId: {}, JobId: {}", recruiter.getProfileId(), jobPostId);
            throw new JobPostNotFound(" Job post not found / Job is not posted by you");
        }

        jobPostRepo.deleteById(jobPostId);

        log.info("Job post deleted successfully. JobId: {}, DeletedBy: {}", jobPostId, recruiter.getProfileId());

        return new ResponseEntity<>("Job Post deleted successfully", HttpStatus.OK);
    }

    public JobPostResponse updateJobPost(UUID jobPostId, JobPostRequest updatedJobPost) {
        log.info("Update request received for JobId: {}", jobPostId);

        Recruiter recruiter =getRecruiter();

        Optional<JobPost> jobPost=jobPostRepo.findByJobIdAndRecruiterProfileId(jobPostId,recruiter.getProfileId());

        if(jobPost.isEmpty()){
            log.warn("Job post not found / Unauthorized update attempt. RecruiterId: {}, JobId: {}", recruiter.getProfileId(), jobPostId);

            throw new JobPostNotFound(" Job Post not found / Job is not posted by you");
        }
        log.debug("Updating job post fields. JobId: {}", jobPostId);

        JobPost post=jobPost.get();

        post.setJobDescription(updatedJobPost.getJobDescription());
        post.setTitle(updatedJobPost.getTitle());
        post.setLocation(updatedJobPost.getLocation());
        post.setNoOfVacancy(updatedJobPost.getNoOfVacancy());
        post.setRequiredEducation(updatedJobPost.getRequiredEducation());
        post.setRequiredSkills(updatedJobPost.getRequiredSkills());
        post.setRequiredExperience(updatedJobPost.getRequiredExperience());
        post.setSalaryRange(updatedJobPost.getSalaryRange());
        post.setCompanyName(updatedJobPost.getCompanyName());

        jobPostRepo.save(post);
        log.info("Job post updated successfully. JobId: {}, UpdatedBy: {}", jobPostId, recruiter.getProfileId());
        return modelMapper.map(post, JobPostResponse.class);
    }
}
