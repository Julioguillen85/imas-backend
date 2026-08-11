package com.martec.imas.agencia.Controllers;

import com.martec.imas.agencia.dto.LoginRequestDTO;
import com.martec.imas.agencia.dto.LoginResponseDTO;
import com.martec.imas.agencia.Security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new LoginResponseDTO(jwt, "Bearer", loginRequest.getUsername()));
    }
}