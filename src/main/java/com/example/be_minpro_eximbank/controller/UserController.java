package com.example.be_minpro_eximbank.controller;

import com.example.be_minpro_eximbank.dto.LoginRequest;
import com.example.be_minpro_eximbank.dto.RegisterRequest;
import com.example.be_minpro_eximbank.dto.UserProfileResponse;
import com.example.be_minpro_eximbank.entity.User;
import com.example.be_minpro_eximbank.repository.RedisRepository;
import com.example.be_minpro_eximbank.service.impl.CustomUserDetailsService;
import com.example.be_minpro_eximbank.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private CustomUserDetailsService userDetailsService;  // Injecting your custom UserDetailsService
    private final RedisRepository redisRepository;

    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtEncoder jwtEncoder, CustomUserDetailsService customUserDetailsService, RedisRepository redisRepository) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.userDetailsService = customUserDetailsService;
        this.redisRepository = redisRepository;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Passwords do not match."));
        }

        User user = new User();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        userService.registerUser(user);

        return ResponseEntity.ok(Map.of("message", "User registered successfully."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        User user = (User) userDetails;  // Assuming UserDetails is your custom User class


        // Check if user already has a valid JWT in Redis
        String existingJwt = redisRepository.getJwtKey(loginRequest.getUsername());
        String jwt;

        if (existingJwt != null) {
            // Use existing JWT
            jwt = existingJwt;
        } else {
            // Generate new JWT
            jwt = generateNewJwt(authentication.getName());
            // Store the new JWT in Redis
            redisRepository.saveJwtKey(loginRequest.getUsername(), jwt);
        }

        // Set cookie
        setCookie(response, jwt);

        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "user", Map.of(
                        "name", user.getName(),
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "phoneNumber", user.getPhoneNumber(),
                        "dateOfBirth", user.getDateOfBirth(),
                        "gender", user.getGender()
                ),
                "access_token", jwt
        ));
    }

    private String generateNewJwt(String username) {
        return jwtEncoder.encode(JwtEncoderParameters.from(JwtClaimsSet.builder()
                .subject(username)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .build())).getTokenValue();
    }

    private void setCookie(HttpServletResponse response, String jwt) {
        Cookie cookie = new Cookie("access_token", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(3600);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    // Add logout endpoint to handle JWT invalidation
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        redisRepository.deleteJwtKey(username);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No valid token provided"));
        }

        String username = jwt.getSubject();
        // Verify token exists in Redis
        String storedToken = redisRepository.getJwtKey(username);
        if (storedToken == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Token has been invalidated"));
        }

        User user = userService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        UserProfileResponse profileResponse = UserProfileResponse.fromUser(user);
        return ResponseEntity.ok(profileResponse);
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getUserProfileByUsername(
            @PathVariable String username,
            @AuthenticationPrincipal Jwt jwt) {

        if (jwt == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No valid token provided"));
        }

        // Verify token exists in Redis
        String storedToken = redisRepository.getJwtKey(jwt.getSubject());
        if (storedToken == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Token has been invalidated"));
        }

        User user = userService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Optional: Check if user has permission to view this profile
        String requestingUsername = jwt.getSubject();
        if (!requestingUsername.equals(username)) {
            // You might want to add role-based check here
            return ResponseEntity.status(403).body(Map.of("error", "Not authorized to view this profile"));
        }

        UserProfileResponse profileResponse = UserProfileResponse.fromUser(user);
        return ResponseEntity.ok(profileResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            userService.initiatePasswordReset(email);
            return ResponseEntity.ok()
                    .body("Password reset email sent successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        try {
            userService.resetPassword(token, newPassword);
            return ResponseEntity.ok()
                    .body("Password reset successful.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
}
