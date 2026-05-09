package com.JobPortal.JobPortalBackend.Services;

import com.JobPortal.JobPortalBackend.Exception.FileValidationException;
import com.JobPortal.JobPortalBackend.Model.JobSeeker;
import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.JobSeekerProfileRepo;
import com.JobPortal.JobPortalBackend.SecurityLayer.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;

@Service
public class ResumeService {

    private final S3Client s3Client;
    private final AuthenticationService authenticationService;
    private final JobSeekerProfileRepo jobSeekerProfileRepo;



    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private static final List<String> ALLOWED_TYPES = List.of("application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/msword");

    @Autowired
    ResumeService(S3Client s3Client, AuthenticationService authenticationService, JobSeekerProfileRepo jobSeekerProfileRepo){
        this.authenticationService = authenticationService;
        this.s3Client = s3Client;
        this.jobSeekerProfileRepo = jobSeekerProfileRepo;
    }

    public String uploadResume(MultipartFile file) {
        Users users = authenticationService.getLoggedInUser();
        System.out.println(file.getOriginalFilename());
        String fileName = users.getUsername() + "_Resume_"+file.getOriginalFilename();
        JobSeeker jobSeeker = jobSeekerProfileRepo.findByUserUserId(users.getUserId()).orElseThrow(()->new RuntimeException("Job seeker profile not found"));

        PutObjectRequest putObjectRequest=PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        jobSeeker.setResume(fileName);
        jobSeekerProfileRepo.save(jobSeeker);

        return "Resume uploaded successfully";

    }


    public Boolean validateFile(MultipartFile file){
        String fileName=file.getOriginalFilename();
        System.out.println(file.getContentType());

        if(file.isEmpty()){
            throw new FileValidationException("Empty file");
        }

        if(file.getContentType()==null || !ALLOWED_TYPES.contains(file.getContentType())){

            throw new FileValidationException("Only PDF, Docx files are allowed");
        }

        if(fileName==null || !(fileName.endsWith(".pdf") || fileName.endsWith(".docx"))){
            throw new FileValidationException("Invalid file name / Only PDF, Docx files are allowed ");
        }

        return true;
    }


    public byte[] getResume(String resumeFileName) {

        GetObjectRequest request=GetObjectRequest.builder()
                .bucket(bucketName)
                .key(resumeFileName)
                .build();

        ResponseInputStream<GetObjectResponse> response=s3Client.getObject(request);


        try {
           return response.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public String deleteResume() {
        Users user=authenticationService.getLoggedInUser();
        JobSeeker jobSeeker =jobSeekerProfileRepo.findByUserUserId(user.getUserId()).orElseThrow(()->new RuntimeException("Job seeker profile not found"));
        String fileName= jobSeeker.getResume();
        DeleteObjectRequest deleteObjectRequest=DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);

        jobSeeker.setResume(null);
        jobSeekerProfileRepo.save(jobSeeker);
        return "Resume deleted successfully";
    }
}
