package com.onirutla.open_music_api.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.atTrace()
                .setMessage("initiating securityFilterChain")
                .addKeyValue("class", "SecurityConfig")
                .log();
        DefaultSecurityFilterChain defaultSecurityFilterChain = http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/albums/**").permitAll()
                        .requestMatchers("/songs/**").permitAll()
                        .requestMatchers("/users/**").permitAll()
                        .requestMatchers("/authentications/**").permitAll()
                        .requestMatchers("/playlists/**").authenticated()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
        log.atTrace()
                .setMessage("securityFilterChain initiated")
                .addKeyValue("class", "SecurityConfig")
                .log();
        return defaultSecurityFilterChain;
    }

}
