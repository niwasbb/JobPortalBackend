package com.JobPortal.JobPortalBackend.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
public class RecruiterProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String profileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false,unique = true)
    private Users user;

    private String fullName;

    private String companyName;

    @OneToMany(mappedBy = "recruiterProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobPost> jobPosts;




}
