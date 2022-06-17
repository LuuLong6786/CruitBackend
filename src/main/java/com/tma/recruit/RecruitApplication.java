package com.tma.recruit;

import com.tma.recruit.model.entity.UserEntity;
import com.tma.recruit.model.enums.UserRole;
import com.tma.recruit.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class RecruitApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecruitApplication.class, args);
    }

    @Bean
    public CommandLineRunner demoData(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (!userRepository.existsByEmailIgnoreCase("admin@tma.com.vn"))
            userRepository.save(new UserEntity("admin@tma.com.vn", encoder.encode("12341234"), UserRole.ADMIN));
        };
    }
}
