package com.JobPortal.JobPortalBackend.DTO;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class JobSeekerDTO {

    private String profileId;

    private String fullName;

    private String phoneNumber;

    private String location;

    private String skills;

    private String education;

    private String experience;

    private String resumeUrl;
}
