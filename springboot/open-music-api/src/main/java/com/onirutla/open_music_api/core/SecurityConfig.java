package com.onirutla.open_music_api.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(15);

    @Bean
    public PasswordEncoder passwordEncoder() {
        return passwordEncoder;
    }
}
