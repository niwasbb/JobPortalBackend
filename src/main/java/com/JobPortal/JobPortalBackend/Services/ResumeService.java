package com.JobPortal.JobPortalBackend.Services;

import com.JobPortal.JobPortalBackend.Exception.FileValidationException;
import com.JobPortal.JobPortalBackend.Model.JobSeeker;
import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.JobSeekerProfileRepo;
import com.JobPortal.JobPortalBackend.SecurityService.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResumeService {

    private final S3Client s3Client;
    private final AuthenticationService authenticationService;
    private final JobSeekerProfileRepo jobSeekerProfileRepo;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private static final List<String> ALLOWED_TYPES = List.of("application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/msword");


    public String uploadResume(MultipartFile file) {
        Users users = authenticationService.getLoggedInUser();

        log.info("Resume upload requested by user: {}", users.getUsername());

        String fileName = users.getUsername() + "_Resume_"+file.getOriginalFilename();
        JobSeeker jobSeeker = jobSeekerProfileRepo.findByUserUserId(users.getUserId()).orElseThrow(()->new RuntimeException("Job seeker profile not found"));

        log.info("Uploading file to S3. Bucket: {}, Key: {}", bucketName, fileName);

        PutObjectRequest putObjectRequest=PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            log.info("Resume uploaded successfully. User: {}, Key: {}", users.getUsername(), fileName);

        } catch (S3Exception e) {
            log.error("S3 upload failed. Bucket: {}, Key: {} Message: {}", bucketName, fileName, e.awsErrorDetails().errorMessage(),e);
            throw e;

        } catch (IOException e) {
            log.error("Failed to read uploaded file bytes. File: {}", fileName, e);
            throw new RuntimeException(e);

        }

        jobSeeker.setResume(fileName);
        jobSeekerProfileRepo.save(jobSeeker);

        log.info("Resume information saved in database for userId: {}", users.getUserId());

        return "Resume uploaded successfully";
    }


    public Boolean validateFile(MultipartFile file){
        log.info("Validating file. Name: {}, Type: {}, Size: {} bytes", file.getOriginalFilename(), file.getContentType(), file.getSize());

        String fileName=file.getOriginalFilename();
        System.out.println(file.getContentType());

        if(file.isEmpty()){
            log.warn("File validation failed: Empty file");
            throw new FileValidationException("Empty file");
        }

        if(file.getContentType()==null || !ALLOWED_TYPES.contains(file.getContentType())){
            log.warn("File validation failed: Invalid content type {}", file.getContentType());
            throw new FileValidationException("Only PDF, Docx files are allowed");
        }

        if(fileName==null || !(fileName.endsWith(".pdf") || fileName.endsWith(".docx"))){
            log.warn("File validation failed: Invalid filename {}", fileName);
            throw new FileValidationException("Invalid file name / Only PDF, Docx files are allowed ");
        }
        log.info("File validation successful: {}", fileName);
        return true;
    }


    public byte[] getResume(String resumeFileName) {

        log.info("Resume download requested. Bucket: {}, Key: {}", bucketName, resumeFileName);

        GetObjectRequest request=GetObjectRequest.builder()
                .bucket(bucketName)
                .key(resumeFileName)
                .build();
        try {
        ResponseInputStream<GetObjectResponse> response=s3Client.getObject(request);

            byte[] fileBytes = response.readAllBytes();

            log.info("Resume downloaded successfully. Key: {}, Size: {} bytes", resumeFileName, fileBytes.length);

            return fileBytes;

        } catch (S3Exception e) {

            log.error("S3 download failed. Bucket: {}, Key: {},Message: {}", bucketName, resumeFileName, e.awsErrorDetails().errorMessage(), e);
            throw e;

        } catch (IOException e) {
            log.error("Failed to read S3 response stream. Key: {}", resumeFileName, e);

            throw new RuntimeException("Failed to read resume file",e);
        }


    }

    public String deleteResume() {
        Users user=authenticationService.getLoggedInUser();
        log.info("Resume deletion requested by user: {}", user.getUsername());

        JobSeeker jobSeeker =jobSeekerProfileRepo.findByUserUserId(user.getUserId()).orElseThrow(()->{
                                                                                log.error("Job seeker profile not found for userId: {}", user.getUserId());
                                                                                return new RuntimeException("Job seeker profile not found");
                                                                                });
        String fileName= jobSeeker.getResume();

        log.info("Deleting file from S3. Bucket: {}, Key: {}", bucketName, fileName);

        try {

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .build();

            s3Client.deleteObject(deleteObjectRequest);

            log.info("File deleted successfully from S3. Key: {}", fileName);

        } catch (S3Exception e) {

            log.error("S3 delete failed. Bucket: {}, Key: {}, Message: {}", bucketName, fileName, e.awsErrorDetails().errorMessage(),e);

            throw e;
        }

        jobSeeker.setResume(null);
        jobSeekerProfileRepo.save(jobSeeker);

        log.info("Resume reference removed from database for userId: {}", user.getUserId());

        return "Resume deleted successfully";
    }
}
