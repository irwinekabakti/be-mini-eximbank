package com.example.be_minpro_eximbank.dto;
import com.example.be_minpro_eximbank.entity.User;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserProfileResponse {
    private Long id;
    private String name;
    private String username;
    private String email;
    private String phoneNumber;
    private User.Gender gender;
    private LocalDate dateOfBirth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserProfileResponse fromUser(User user) {
        UserProfileResponse profile = new UserProfileResponse();
        profile.setId(user.getId());
        profile.setName(user.getName());
        profile.setUsername(user.getUsername());
        profile.setEmail(user.getEmail());
        profile.setPhoneNumber(user.getPhoneNumber());
        profile.setGender(user.getGender());
        profile.setDateOfBirth(user.getDateOfBirth());
        profile.setCreatedAt(user.getCreatedAt());
        profile.setUpdatedAt(user.getUpdatedAt());
        return profile;
    }
}