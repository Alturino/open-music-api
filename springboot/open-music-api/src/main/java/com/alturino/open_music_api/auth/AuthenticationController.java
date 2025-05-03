package com.alturino.open_music_api.auth;

import com.alturino.open_music_api.core.JwtService;
import com.alturino.open_music_api.core.exception.BadRequestException;
import com.alturino.open_music_api.user.UserEntity;
import com.alturino.open_music_api.user.UserRepository;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.StringObjectMapBuilder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping(value = "/authentications")
@RequiredArgsConstructor
@RestController
@Slf4j
public class AuthenticationController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<Map<String, Object>> login(@RequestBody @Valid LoginRequest request) {
        log.atInfo()




                .log("finding user by username");
        UserEntity user = userRepository
                .findByUsername(request.username())
                .orElseThrow(() -> {
                    BadCredentialsException e = new BadCredentialsException("username is incorrect");
                    log.atError()
            
            
            
            
                            .setCause(e)
                            .log("username not found");
                    return e;
                });
        log.atInfo()




                .log("user with username {} found", request.username());

        log.atInfo()




                .log("checking is password valid");
        boolean isValidPassword = passwordEncoder.matches(request.password(), user.getPassword());
        if (!isValidPassword) {
            BadCredentialsException e = new BadCredentialsException("password is incorrect");
            log.atError()
    
    
    
    
    
    
                    .setCause(e)
                    .log("password is invalid");
            throw e;
        }
        log.atInfo()




                .log("password is valid");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getId(),
                user.getPassword()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Map<String, Object> claims = new StringObjectMapBuilder()
                .put("id", user.getId())
                .put("username", user.getUsername())
                .put("password", user.getPassword())
                .put("authorities", user.getAuthorities())
                .get();

        log.atInfo()




                .log("initiating access token generation");
        String accessToken = jwtService.generateAccessToken(Jwts.claims().add(claims).build(), user);
        log.atInfo()




                .log("finished access token generation");

        log.atInfo()




                .log("initiating refresh token generation");
        String refreshToken = jwtService.generateRefreshToken(Jwts.claims().add(claims).build(), user);
        log.atInfo()





                .log("finished refresh token generation");

        log.atInfo()





                .log("saving refresh token to database");
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        log.atInfo()





                .log("refresh token saved to database");

        Map<String, Object> data = new StringObjectMapBuilder()
                .put("accessToken", accessToken)
                .put("refreshToken", refreshToken)
                .get();
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("message", "login success")
                .put("data", data)
                .get();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> updateRefreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        log.atInfo()


                .log("initiating update refresh token");

        UserEntity user = userRepository
                .findByRefreshToken(request.refreshToken())
                .orElseThrow(() -> {
                    BadRequestException e = new BadRequestException("refresh token not found");
                    log.atInfo()
            
            
                            .setCause(e)
                            .log(e.getMessage());
                    return e;
                });

        boolean isValid = jwtService.isRefreshTokenKeyValid(request.refreshToken(), user);
        if (!isValid) {
            throw new BadCredentialsException("refresh token is invalid");
        }

        Map<String, Object> claims = new StringObjectMapBuilder()
                .put("id", user.getId())
                .put("username", user.getUsername())
                .put("password", user.getPassword())
                .put("authorities", user.getAuthorities())
                .get();
        String newAccessToken = jwtService.generateAccessToken(Jwts.claims().add(claims).build(), user);
        Map<String, Object> data = new StringObjectMapBuilder()
                .put("accessToken", newAccessToken)
                .get();
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("message", "refresh token success")
                .put("data", data)
                .get();
        return ResponseEntity.ok(body);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteRefreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        log.atInfo()


                .log("initiating delete refresh token");

        log.atInfo()


                .log("finding user by refresh token");
        UserEntity user = userRepository.findByRefreshToken(request.refreshToken())
                .orElseThrow(() -> {
                    BadRequestException e = new BadRequestException("refresh token not found");
                    log.atError()
            
            
                            .setCause(e)
                            .log(e.getMessage());
                    return e;
                });
        log.atInfo()



                .log("user with refresh token {} is found", user.getRefreshToken());

        log.atInfo()



                .log("initiating delete refresh token");
        user.setRefreshToken(null);
        userRepository.save(user);
        log.atInfo()



                .log("refresh token deleted");

        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("message", "delete refresh token success")
                .get();
        return ResponseEntity.ok(body);
    }
}
