package com.JobPortal.JobPortalBackend.Services;

import com.JobPortal.JobPortalBackend.Model.MyUserDetails;
import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Repository.UserRepo;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class MyUserDetailsService implements UserDetailsService {


/*************  Dependencies       ****************************/
    private final UserRepo userRepo;
    @Autowired
    public MyUserDetailsService(UserRepo userRepo){
        this.userRepo=userRepo;
    }
/************************************************************/


    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {

        Users user=userRepo.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found with username: "+username));


        return new MyUserDetails(user);
    }
}
