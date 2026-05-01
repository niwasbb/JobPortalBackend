package com.JobPortal.JobPortalBackend.DTO;


import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UsersDTO {

    private String userId;

    private String username;

    private String email;
}
