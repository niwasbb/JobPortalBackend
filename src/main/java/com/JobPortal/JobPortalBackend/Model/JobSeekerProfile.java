package com.JobPortal.JobPortalBackend.Model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.format.annotation.NumberFormat;

import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
public class JobSeekerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String profileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users user;

    private String fullName;

    @Email
    private String email;

    @Nullable
    @NumberFormat
    private String phoneNumber;

    private String location;

    private String skills;

    private String education;

    private String experience;

    private String resumeUrl;

    @OneToMany(mappedBy = "jobSeekerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplication> jobApplication;


}
