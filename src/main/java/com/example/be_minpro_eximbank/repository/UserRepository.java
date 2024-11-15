package com.example.be_minpro_eximbank.repository;

import com.example.be_minpro_eximbank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByNameContaining(String namePart);
    @Query("SELECT u FROM User u WHERE u.email = ?")
    User findByEmail(String email);
    List<User> findByEnabledTrue();
    List<User> findByEnabledFalse();
}