package com.JobPortal.JobPortalBackend.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@RequiredArgsConstructor
public class Recruiter {

    @Id
    private UUID profileId;

    @OneToOne
    @MapsId
    @JoinColumn(name="User_id", nullable = false,unique = true)
    private Users user;

    @Email
    private String emailId;

    private String firstName;

    private String lastName;

    private String companyName;

    private String  industryType;

    @OneToMany(mappedBy = "recruiter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobPost> jobPosts;

}
