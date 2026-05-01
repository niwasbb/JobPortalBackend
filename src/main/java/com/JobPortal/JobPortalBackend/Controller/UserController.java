package com.JobPortal.JobPortalBackend.Controller;


import com.JobPortal.JobPortalBackend.Model.Users;
import com.JobPortal.JobPortalBackend.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/")
public class UserController {

/*********    dependencies ***************/

    private final UserService userService;
    private final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(12);

    @Autowired
    public UserController(UserService userService){

        this.userService=userService;
    }



/****************************************/


    @PostMapping("/register")
    public void newUser(@Valid @RequestBody Users user){
        user.setPassword(encoder.encode( user.getPassword()));
        userService.newUser(user);
    }



    @PostMapping("/login")
    public String userLogin(@RequestBody Users user){
        return userService.verifyUser(user);
    }


    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") String userId){
        userService.deleteUserById(userId);

        return new ResponseEntity<>("User with id "+userId+" deleted successfully", org.springframework.http.HttpStatus.OK);
    }


}
