package com.JobPortal.JobPortalBackend.ServiceTest;


import com.JobPortal.JobPortalBackend.Model.JobSeeker;
import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.JobSeekerProfileRepo;
import com.JobPortal.JobPortalBackend.SecurityLayer.AuthenticationService;
import com.JobPortal.JobPortalBackend.Services.ResumeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResumeServiceTest {

    @InjectMocks
    ResumeService resumeService;

    @Mock
    private S3Client s3Client;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private JobSeekerProfileRepo jobSeekerProfileRepo;
    @Mock
    MultipartFile resumeFile;

    @Test
    public void uploadResumeTest() throws IOException {

        byte[] bytes="dummy data".getBytes();


        Users user=new Users();
        user.setUsername("abc");
        user.setUserId(UUID.randomUUID());
        JobSeeker jobSeeker=new JobSeeker();
        jobSeeker.setUser(user);

        when(authenticationService.getLoggedInUser()).thenReturn(user);
        when(jobSeekerProfileRepo.findByUserUserId(user.getUserId())).thenReturn(Optional.of(jobSeeker));
        when(resumeFile.getBytes()).thenReturn(bytes);
        when(resumeFile.getOriginalFilename()).thenReturn("resume.pdf");
        when(resumeFile.getContentType()).thenReturn("application/pdf");

        String response=resumeService.uploadResume(resumeFile);

        Assertions.assertEquals("Resume uploaded successfully",response);
        verify(authenticationService,times(1)).getLoggedInUser();
        verify(jobSeekerProfileRepo,times(1)).findByUserUserId(user.getUserId());
        verify(jobSeekerProfileRepo, times(1)).save(jobSeeker);
        verify(s3Client,times(1)).putObject(any(PutObjectRequest.class),any(RequestBody.class));
        Assertions.assertEquals("abc_Resume_resume.pdf",jobSeeker.getResume());

    }

    @Test
    public void uploadResumeExceptionTest() throws IOException {
        Users user=new Users();
        user.setUsername("abc");
        user.setUserId(UUID.randomUUID());
        JobSeeker jobSeeker=new JobSeeker();
        jobSeeker.setUser(user);

        when(authenticationService.getLoggedInUser()).thenReturn(user);
        when(jobSeekerProfileRepo.findByUserUserId(user.getUserId())).thenReturn(Optional.of(jobSeeker));
        when(resumeFile.getContentType()).thenReturn("application/pdf");
        when(resumeFile.getOriginalFilename()).thenReturn("resume.pdf");
        when(resumeFile.getBytes()).thenThrow(new IOException());

        Assertions.assertThrows(RuntimeException.class,()->resumeService.uploadResume(resumeFile));
    }

    @Test
    public void getResumeTest(){
        String resumeFileName="Resume.docx";

        byte[] bytes="Dummy data".getBytes();


        InputStream inputStream= new ByteArrayInputStream(bytes);

        ResponseInputStream<GetObjectResponse> responseResponseInputStream=new ResponseInputStream<>(
                GetObjectResponse.builder().build(), AbortableInputStream.create(inputStream));


        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseResponseInputStream);

        byte[] receivedBytes =resumeService.getResume(resumeFileName);

        Assertions.assertArrayEquals(bytes,receivedBytes);
        verify(s3Client).getObject(any(GetObjectRequest.class));
    }

    @Test
    public void getResumeExceptionTest(){
        InputStream failingStream=new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Read failed");
            }
        };

        ResponseInputStream<GetObjectResponse> responseResponseInputStream=new ResponseInputStream<>(
                GetObjectResponse.builder().build(), AbortableInputStream.create(failingStream));

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseResponseInputStream);

        Assertions.assertThrows(RuntimeException.class,()->resumeService.getResume("Resume.pdf"));



    }

    @Test
    public void deleteResumeTest() throws IOException {
        byte[] bytes="dummy data".getBytes();

        Users user=new Users();
        user.setUsername("abc");
        user.setUserId(UUID.randomUUID());
        JobSeeker jobSeeker=new JobSeeker();
        jobSeeker.setUser(user);
        jobSeeker.setResume("abc_Resume_resume.pdf");

        when(authenticationService.getLoggedInUser()).thenReturn(user);
        when(jobSeekerProfileRepo.findByUserUserId(user.getUserId())).thenReturn(Optional.of(jobSeeker));

        resumeService.deleteResume();

        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
        verify(jobSeekerProfileRepo,times(1)).save(any(JobSeeker.class));
    }



}
