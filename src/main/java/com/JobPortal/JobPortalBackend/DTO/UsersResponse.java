package com.JobPortal.JobPortalBackend.DTO;


import com.JobPortal.JobPortalBackend.Model.UserRole;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class UsersResponse {

    private UUID userId;

    private String username;

    private String emailId;

    private UserRole role;

}
