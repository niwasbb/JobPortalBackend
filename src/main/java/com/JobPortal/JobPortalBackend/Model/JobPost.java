package com.JobPortal.JobPortalBackend.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.util.List;


@Entity
@Data
@RequiredArgsConstructor
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String jobId;
    
    @CreationTimestamp
    private Instant postedDate;

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
