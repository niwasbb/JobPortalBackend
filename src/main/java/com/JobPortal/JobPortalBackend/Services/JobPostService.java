package com.JobPortal.JobPortalBackend.Services;

import com.JobPortal.JobPortalBackend.DTO.DetailedJobPostDTO;
import com.JobPortal.JobPortalBackend.DTO.JobPostDTO;
import com.JobPortal.JobPortalBackend.Exception.JobPostNotFound;
import com.JobPortal.JobPortalBackend.Model.JobPost;
import com.JobPortal.JobPortalBackend.Model.RecruiterProfile;
import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.JobPostRepo;
import com.JobPortal.JobPortalBackend.Repository.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class JobPostService {

    private final JobPostRepo jobPostRepo;
    private final ModelMapper modelMapper;
    private final UserRepo userRepo;

    @Autowired
    public JobPostService(JobPostRepo jobPostRepo,ModelMapper modelMapper,UserRepo userRepo){

        this.jobPostRepo=jobPostRepo;
        this.modelMapper=modelMapper;
        this.userRepo=userRepo;
    }



    public Page<JobPostDTO> getJobs(Pageable pageable){

        Page<JobPost> jobPostPage= jobPostRepo.findAll(pageable);

        return jobPostPage.map(jobPost ->
            new JobPostDTO(jobPost.getJobId(),
                    jobPost.getTitle(),
                    jobPost.getCompanyName(),
                    jobPost.getLocation(),
                    jobPost.getPostedDate())
        );
    }


    public DetailedJobPostDTO getJobPostById(String id) {

        JobPost jobPost= jobPostRepo.findById(id).orElseThrow(()-> new JobPostNotFound("Job Post not found with id: "+id));
        return modelMapper.map(jobPost,DetailedJobPostDTO.class);

    }

    public DetailedJobPostDTO postNewJob(DetailedJobPostDTO newJobPost) {

        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        String loggedinUserName=auth.getName();
        Users user= userRepo.findByUsername(loggedinUserName).orElseThrow(()-> new RuntimeException("Logged in user not found"));

        if(auth.getAuthorities().stream().noneMatch(a->a.getAuthority().equals("ROLE_RECRUITER"))){
            throw new AuthorizationServiceException(HttpStatus.UNAUTHORIZED+" Only recruiters can post jobs");
        }

        JobPost jobPost=modelMapper.map(newJobPost,JobPost.class);
        jobPost.setRecruiterProfile(user.getRecruiterProfile());

        JobPost savedJobPost= jobPostRepo.save(jobPost);
        return modelMapper.map(savedJobPost,DetailedJobPostDTO.class);
    }
}
