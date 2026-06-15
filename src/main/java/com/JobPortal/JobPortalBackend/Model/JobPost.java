package com.JobPortal.JobPortalBackend.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Entity
@Data
@RequiredArgsConstructor
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID jobId;
    
    @CreationTimestamp
    private LocalDateTime postedDate;

    @NotBlank
    private String title;

    @Column(columnDefinition = "TEXT")
    private String jobDescription;

    @NotBlank
    private String companyName;

    @NotBlank
    private String location;

    @ElementCollection
    private List<String> requiredSkills;

    private String requiredEducation;

    private String requiredExperience;

    private int noOfVacancy;

    private String salaryRange;

    @ManyToOne
    @JoinColumn(name = "Recruiter_id",nullable = false)
    private Recruiter recruiter;

    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplication> jobApplications;


}
