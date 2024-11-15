package com.example.be_minpro_eximbank.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Disable CSRF (not recommended for production without proper consideration)
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/v1/users/**").authenticated() // Protect endpoints requiring authentication
                        .requestMatchers("/api/v1/users/**").permitAll()
                        .anyRequest().permitAll() // Allow other endpoints without authentication
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
