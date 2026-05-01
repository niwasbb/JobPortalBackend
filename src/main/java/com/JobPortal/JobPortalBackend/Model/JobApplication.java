package com.JobPortal.JobPortalBackend.Model;

import jakarta.persistence.*;

@Entity
public class JobApplication {

    @Id
    private String applicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private JobPost jobPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Applicant_id", nullable = false)
    private JobSeekerProfile jobSeekerProfile;

    private  ApplicationStatus status;
}
