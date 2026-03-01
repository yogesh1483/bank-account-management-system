package com.bank.management.app.controller;

import com.bank.management.app.dto.RequestDTO.LoginRequestDTO;
import com.bank.management.app.dto.ResponseDTO.LoginResponseDTO;
import com.bank.management.app.entity.Customer;
import com.bank.management.app.exception.ResourceNotFoundException;
import com.bank.management.app.repository.CustomerRepository;
import com.bank.management.app.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CustomerRepository customerRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO requestDTO){
        Customer customer = customerRepository.findByUsername(requestDTO.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Credentials"));

        // 🔐 BCrypt password check
        if (!passwordEncoder.matches(requestDTO.getPassword(), customer.getPassword())) {
            throw new ResourceNotFoundException("Invalid credentials");
        }

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDTO.getUsername(),
                        requestDTO.getPassword()
                ));

        String token = jwtUtil.generateToken(authenticate);

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logged out successfully");
    }
}
