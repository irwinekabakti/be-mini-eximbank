package com.example.be_minpro_eximbank.service.impl;
import com.example.be_minpro_eximbank.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

//@Service("customUserDetailsService")  // Use a custom name here
@Primary  // Mark as the primary bean
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        // Fetch user from database
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        return org.springframework.security.core.userdetails.User
//                .withUsername(user.getUsername())
//                .password(user.getPassword())
//                .authorities("USER") // You can adjust authorities or roles as per your need
//                .build();
//    }
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.be_minpro_eximbank.entity.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user; // Returning the custom User entity that implements UserDetails
    }
}
