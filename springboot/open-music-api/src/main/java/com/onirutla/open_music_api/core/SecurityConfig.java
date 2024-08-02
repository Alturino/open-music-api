package com.onirutla.open_music_api.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain albums(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests((authorizeRequests) -> {
            authorizeRequests.requestMatchers("/albums")
                    .anonymous()
                    .anyRequest()
                    .authenticated();
        }).build();
    }

}
