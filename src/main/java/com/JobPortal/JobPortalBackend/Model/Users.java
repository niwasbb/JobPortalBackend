package com.JobPortal.JobPortalBackend.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;

    @NotBlank(message = "Username should be at least 5 characters long")
    @Column(nullable = false, unique = true)
    private String username;

    @Email(message = "Enter valid email")
    @NotBlank(message = "Enter valid email")
    @Column(nullable = false, unique = true)
    private String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$", message = "Password should be 8 characters long, should have minimum 1 small letter (a-z), 1 capital letter (A-Z), 1 special character @$!%*?& , 1 digit (0-9).")
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToOne(mappedBy = "user",  cascade = CascadeType.ALL,orphanRemoval = true)
    private JobSeekerProfile jobSeekerProfile;

    @OneToOne(mappedBy = "user",  cascade = CascadeType.ALL,orphanRemoval = true)
    private RecruiterProfile recruiterProfile;
}
