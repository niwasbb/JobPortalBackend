package com.JobPortal.JobPortalBackend.DTO;

import com.JobPortal.JobPortalBackend.Model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserRequest {

    @NotBlank(message = "Username should be at least 5 characters long")
    @Size(min = 5, message = "Username should be at least 5 characters long")
    private String username;

    @Email(message = "Enter valid email")
    @NotBlank(message = "Enter valid email")
    private String emailId;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$", message = "Password should be 8 characters long, should have minimum 1 small letter (a-z), 1 capital letter (A-Z), 1 special character @$!%*?& , 1 digit (0-9).")
    private String password;


    private UserRole role;
}
