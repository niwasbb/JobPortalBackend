package com.JobPortal.JobPortalBackend.Model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.NumberFormat;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@RequiredArgsConstructor
public class JobSeeker {

    @Id
    private UUID profileId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "User_id", nullable = false, unique = true)
    private Users user;

    private String firstName;

    private String lastName;

    @Email
    private String emailId;

    @Nullable
    @NumberFormat
    private String phoneNumber;

    private String location;

    private List<String> skills;

    private String education;

    private String experience;

    private String resume;

    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplication> appliedJobs;

}
