package com.JobPortal.JobPortalBackend.SecurityLayer;

import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.UserRepo;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepo userRepo;

    AuthenticationService(UserRepo userRepo){
        this.userRepo=userRepo;
    }

    public Users getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = auth.getName();
        return userRepo.findByUsername(loggedInUsername).orElseThrow(() -> new AuthenticationCredentialsNotFoundException("login first"));
    }
}
