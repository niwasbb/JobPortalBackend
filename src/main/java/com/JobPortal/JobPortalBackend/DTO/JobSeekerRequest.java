package com.JobPortal.JobPortalBackend.DTO;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class JobSeekerRequest {

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String location;

    private List<String> skills;

    private String education;

    private String experience;


}
