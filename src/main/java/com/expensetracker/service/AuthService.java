package com.expensetracker.service;

import com.expensetracker.dto.RequestDto;
import com.expensetracker.dto.ResponseDto;
import com.expensetracker.entity.User;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository        userRepository;
    @Autowired
    private PasswordEncoder       passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil               jwtUtil;

    public ResponseEntity<?> signUp(RequestDto requestDto) {
        try {
            if (requestDto == null || requestDto.getUsername() == null || requestDto.getPassword() == null) {
                LOGGER.error("Invalid request data");
                return ResponseEntity.badRequest().body("Invalid request data");
            }
            if (userRepository.findByUsername(requestDto.getUsername()).isPresent()) {
                LOGGER.error("Username already exists");
                return ResponseEntity.status(409).body("Username already exists");
            }
            User user = User.builder().username(requestDto.getUsername()).password(passwordEncoder.encode(requestDto.getPassword())).build();
            if (userRepository.save(user).getId() > 0) {
                LOGGER.info("User signed up successfully");
                return ResponseEntity.ok("User signed up successfully");
            } else {
                LOGGER.error("Failed to sign up user");
                return ResponseEntity.status(500).body("Failed to sign up user");
            }
        } catch (Exception e) {
            LOGGER.error("Error in signUp() method !!", e);
        }
        return ResponseEntity.status(500).body("Internal Server Error");
    }

    public ResponseEntity<?> login(RequestDto requestDto) {
        try {
            if (requestDto == null || requestDto.getUsername() == null || requestDto.getPassword() == null) {
                LOGGER.error("Invalid request data, while login");
                return ResponseEntity.badRequest().body("Invalid request data");
            }
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword()));
            String token = jwtUtil.generateToken(requestDto.getUsername());
            return new ResponseEntity<>(new ResponseDto(token), HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            LOGGER.error("Error in login() method !!", e);
        }
        return ResponseEntity.status(500).body("Internal Server Error");
    }
}
