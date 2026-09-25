package com.example.EduGraph.config;

import com.example.EduGraph.entity.User;
import com.example.EduGraph.enums.AccountStatus;
import com.example.EduGraph.enums.UserRole;
import com.example.EduGraph.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String adminEmail = "yonexffyt14@gmail.com";
        Optional<User> existing = userRepository.findByEmail(adminEmail);

        if (existing.isEmpty()) {
            User admin = User.builder()
                    .fullName("EduGraph Super Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(UserRole.ADMIN)
                    .status(AccountStatus.ACTIVE)
                    .phoneNumber("+91 99999 88888")
                    .build();
            userRepository.save(admin);
            log.info(">>> DataInitializer: Seeded Super Admin with email: {}", adminEmail);
        } else {
            User admin = existing.get();
            admin.setRole(UserRole.ADMIN);
            admin.setStatus(AccountStatus.ACTIVE);
            userRepository.save(admin);
            log.info(">>> DataInitializer: Verified and elevated user {} to ADMIN role", adminEmail);
        }
    }
}