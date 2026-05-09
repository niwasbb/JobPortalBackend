package com.JobPortal.JobPortalBackend.Controller;


import com.JobPortal.JobPortalBackend.DTO.LoginRequest;
import com.JobPortal.JobPortalBackend.DTO.UserRequest;
import com.JobPortal.JobPortalBackend.DTO.UsersResponse;
import com.JobPortal.JobPortalBackend.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {

/*********    dependencies ***************/

    private final UserService userService;
    private final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(12);

    @Autowired
    public UserController(UserService userService){

        this.userService=userService;
    }

//****************************************//

    @GetMapping("/user")
    public ResponseEntity<UsersResponse> getUser(){
        UsersResponse user=userService.getUser();
        return ResponseEntity.ok(user);
    }
    @PostMapping("/register")
    public ResponseEntity<String> newUser(@Valid @RequestBody UserRequest user){
        user.setPassword(encoder.encode( user.getPassword()));
        return userService.newUser(user);
    }

    @PostMapping("/login")
    public String userLogin(@RequestBody LoginRequest loginRequest){
        return userService.userLogin(loginRequest);
    }


    @DeleteMapping("/user")
    public ResponseEntity<String> deleteAccount(){
        String username=userService.deleteAccount();

        return new ResponseEntity<>("User "+username+" deleted successfully", org.springframework.http.HttpStatus.OK);
    }

}
