package com.example.be_minpro_eximbank.dto;
import com.example.be_minpro_eximbank.entity.User;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UserDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Username is required")
    @Size(min = 4, message = "Username must be at least 4 characters")
    private String username;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must be at least 8 characters and include uppercase, lowercase, number and special character")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number")
    private String phoneNumber;

    @NotNull(message = "Gender is required")
    private User.Gender gender;

    @Past(message = "Date of birth must be in the past")
    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;
}
