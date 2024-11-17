package com.example.be_minpro_eximbank.dto;

import com.example.be_minpro_eximbank.entity.User.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {
    private String name;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Gender gender;
}

