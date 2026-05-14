package com.JobPortal.JobPortalBackend.Controller;


import com.JobPortal.JobPortalBackend.DTO.LoginRequest;
import com.JobPortal.JobPortalBackend.DTO.UserRequest;
import com.JobPortal.JobPortalBackend.DTO.UsersResponse;
import com.JobPortal.JobPortalBackend.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {

/*********    dependencies ***************/

    private final UserService userService;

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
        String response= userService.newUser(user);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> userLogin(@RequestBody LoginRequest loginRequest){
        String result= userService.userLogin(loginRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @DeleteMapping("/user")
    public ResponseEntity<String> deleteAccount(){
        String username=userService.deleteAccount();
        return new ResponseEntity<>("User "+username+" deleted successfully", HttpStatus.OK);
    }

}
