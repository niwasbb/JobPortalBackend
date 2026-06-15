package com.JobPortal.JobPortalBackend.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;


@Data
@Entity
@RequiredArgsConstructor
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID applicationId;

    @ManyToOne
    @JoinColumn(name = "Job_Post_id", nullable = false)
    private JobPost jobPost;

    @ManyToOne
    @JoinColumn(name = "Applicant_id", nullable = false)
    private JobSeeker jobSeeker;

    @Enumerated(EnumType.STRING)
    private  ApplicationStatus status;

    private String resume;

}
