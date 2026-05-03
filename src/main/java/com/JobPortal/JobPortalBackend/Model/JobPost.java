package com.JobPortal.JobPortalBackend.Model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@RequiredArgsConstructor
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String jobId;
    
    @CreationTimestamp
    private LocalDateTime postedDate;

    @NotBlank
    private String title;

    private String jobDescription;

    @NotBlank
    private String companyName;

    @NotBlank
    private String location;

    @NotBlank
    private String requiredSkills;

    @NotBlank
    private String requiredEducation;

    private int noOfVacancy=1;

    private String salaryRange;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id",nullable = false)
    private RecruiterProfile recruiterProfile;

    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplication> jobApplications;


}
