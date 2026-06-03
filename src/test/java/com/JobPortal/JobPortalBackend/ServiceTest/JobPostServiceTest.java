package com.JobPortal.JobPortalBackend.ServiceTest;

import com.JobPortal.JobPortalBackend.DTO.JobPostRequest;
import com.JobPortal.JobPortalBackend.DTO.JobPostResponse;
import com.JobPortal.JobPortalBackend.Exception.JobPostNotFound;
import com.JobPortal.JobPortalBackend.Model.JobPost;
import com.JobPortal.JobPortalBackend.Model.Recruiter;
import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.JobPostRepo;
import com.JobPortal.JobPortalBackend.SecurityService.AuthenticationService;
import com.JobPortal.JobPortalBackend.Services.JobPostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobPostServiceTest {

    @Mock
    private JobPostRepo jobPostRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AuthenticationService authenticationService;


    @InjectMocks
    private JobPostService jobPostService;

    private Recruiter creatRecruiter(){
        JobPost jobPost=new JobPost();
        jobPost.setJobId(UUID.randomUUID());
        List<JobPost> jobPosts=List.of(jobPost);
        Recruiter recruiter = new Recruiter();
        recruiter.setProfileId(UUID.randomUUID());
        recruiter.setJobPosts(jobPosts);
        return recruiter;
    }



    @Test
    void getJobs_ShouldReturnAllJobs_WhenSearchByIsNull() {

        Pageable pageable = PageRequest.of(0, 10);

        Recruiter recruiter = new Recruiter();
        recruiter.setCompanyName("ABC");

        JobPost jobPost = new JobPost();
        jobPost.setRecruiter(recruiter);

        Page<JobPost> page = new PageImpl<>(List.of(jobPost));

        when(jobPostRepo.findAll(pageable)).thenReturn(page);

        Page<JobPostResponse> result = jobPostService.getJobs(null, pageable);

        assertEquals(1, result.getTotalElements());

        verify(jobPostRepo).findAll(pageable);
    }

    @Test
    void getJobs_ShouldSearchJobs_WhenKeywordProvided() {

        Pageable pageable = PageRequest.of(0, 10);

        Recruiter recruiter = new Recruiter();
        recruiter.setCompanyName("ABC");

        JobPost jobPost = new JobPost();
        jobPost.setRecruiter(recruiter);

        Page<JobPost> page = new PageImpl<>(List.of(jobPost));

        when(jobPostRepo.searchByKeyword("java", pageable)).thenReturn(page);

        Page<JobPostResponse> result = jobPostService.getJobs("java", pageable);

        assertEquals(1, result.getTotalElements());

        verify(jobPostRepo).searchByKeyword("java", pageable);
    }

    @Test
    void postNewJob_ShouldCreateJobSuccessfully() {

        Users user = new Users();
        UUID userId = UUID.randomUUID();
        user.setUserId(userId);
        user.setRecruiter(creatRecruiter());

        JobPostRequest request = new JobPostRequest();
        JobPost jobPost = new JobPost();

        JobPostResponse response = new JobPostResponse();

        when(authenticationService.getLoggedInUser()).thenReturn(user);

        when(modelMapper.map(request, JobPost.class)).thenReturn(jobPost);

        when(jobPostRepo.save(jobPost)).thenReturn(jobPost);

        when(modelMapper.map(jobPost, JobPostResponse.class)).thenReturn(response);

        JobPostResponse result = jobPostService.postNewJob(request);

        assertNotNull(result);

        verify(jobPostRepo).save(jobPost);
    }


    @Test
    void getJobPostById_ShouldReturnJob() {

        UUID id = UUID.randomUUID();

        JobPost jobPost = new JobPost();
        JobPostResponse response = new JobPostResponse();

        when(jobPostRepo.findById(id)).thenReturn(Optional.of(jobPost));

        when(modelMapper.map(jobPost, JobPostResponse.class)).thenReturn(response);

        JobPostResponse result = jobPostService.getJobPostById(id);

        assertNotNull(result);
    }

    @Test
    void getJobPostById_ShouldThrowException_WhenJobNotFound() {

        UUID id = UUID.randomUUID();

        when(jobPostRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(JobPostNotFound.class, () -> jobPostService.getJobPostById(id));
    }

    @Test
    void deleteJobPost_ShouldDeleteSuccessfully() {

        UUID jobId = UUID.randomUUID();
        JobPost jobPost = new JobPost();
        jobPost.setJobId(jobId);
        List<JobPost> jobPosts=List.of(jobPost);

        Users user = new Users();
        user.setUserId(UUID.randomUUID());

        Recruiter recruiter= creatRecruiter();
        recruiter.setJobPosts(jobPosts);
        user.setRecruiter(recruiter);

        when(jobPostRepo.existsByJobIdAndRecruiterProfileId(jobId,recruiter.getProfileId())).thenReturn(true);
        when(authenticationService.getLoggedInUser()).thenReturn(user);

        ResponseEntity<String> response = jobPostService.deleteJobPost(jobId);

        assertEquals(200, response.getStatusCode().value());

        verify(jobPostRepo).deleteById(jobId);
    }

    @Test
    void deleteJobPost_ShouldThrowException_WhenJobNotFound() {

        UUID jobId = UUID.randomUUID();
        Recruiter recruiter=creatRecruiter();
        Users user =new Users();
        user.setRecruiter(recruiter);

        when(authenticationService.getLoggedInUser()).thenReturn(user);
        when(jobPostRepo.existsByJobIdAndRecruiterProfileId(jobId,recruiter.getProfileId())).thenReturn(false);

        assertThrows(JobPostNotFound.class, () -> jobPostService.deleteJobPost(jobId));
    }



    @Test
    void updateJobPost_ShouldUpdateSuccessfully() {

        UUID jobId = UUID.randomUUID();
        List<JobPost> jobPosts=List.of(new JobPost());

        Users user = new Users();
        user.setUserId(UUID.randomUUID());

        Recruiter recruiter = new Recruiter();
        recruiter.setProfileId(UUID.randomUUID());
        recruiter.setJobPosts(jobPosts);
        user.setRecruiter(recruiter);
        JobPost jobPost = new JobPost();

        JobPostRequest request = new JobPostRequest();
        JobPostResponse response = new JobPostResponse();

        when(authenticationService.getLoggedInUser()).thenReturn(user);
        when(jobPostRepo.findByJobIdAndRecruiterProfileId(jobId,recruiter.getProfileId())).thenReturn(Optional.of(jobPost));
        when(modelMapper.map(jobPost,JobPostResponse.class)).thenReturn(response);

        JobPostResponse result = jobPostService.updateJobPost(jobId, request);

        assertNotNull(result);

        verify(jobPostRepo).save(jobPost);
    }

    @Test
    void updateJobPost_ShouldThrowJobNotFound() {

        UUID jobId = UUID.randomUUID();
        Users user=new Users();
        Recruiter recruiter=creatRecruiter();
        user.setRecruiter(recruiter);
        when(authenticationService.getLoggedInUser()).thenReturn(user);
        when(jobPostRepo.findByJobIdAndRecruiterProfileId(jobId,recruiter.getProfileId())).thenReturn(Optional.empty());

        assertThrows(JobPostNotFound.class, () -> jobPostService.updateJobPost(jobId, new JobPostRequest()));
    }

}