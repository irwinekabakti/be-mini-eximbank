package com.example.be_minpro_eximbank.entity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @SequenceGenerator(name = "users_seq", sequenceName = "users_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private boolean enabled = false;

    @Column(name = "verification_token", unique = true, length = 100)
    private String verificationToken;

    @Column(name = "verification_token_expiry")
    private LocalDateTime verificationTokenExpiry;

    @Column(name = "password_reset_token", unique = true, length = 100)
    private String passwordResetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}