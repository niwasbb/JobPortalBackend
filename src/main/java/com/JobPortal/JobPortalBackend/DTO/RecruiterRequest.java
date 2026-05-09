package com.JobPortal.JobPortalBackend.DTO;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RecruiterRequest {

    private String firstName;

    private String lastName;

    private String companyName;

    private String industryType;
}
