package com.JobPortal.JobPortalBackend.Model;

import com.JobPortal.JobPortalBackend.DTO.JobPostDTO;
import com.JobPortal.JobPortalBackend.DTO.JobSeekerDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@Entity
@RequiredArgsConstructor
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String applicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private JobPost jobPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Applicant_id", nullable = false)
    private JobSeekerProfile jobSeekerProfile;

    @Enumerated(EnumType.STRING)
    private  ApplicationStatus status;

}
