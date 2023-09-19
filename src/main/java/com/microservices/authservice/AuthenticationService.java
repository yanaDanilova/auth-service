package com.microservices.authservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service

public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @Autowired
    public AuthenticationService(UserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public JwtTokenResponse register(AuthenticationRequest request) {
        User user = new User(request.getUsername(), passwordEncoder.encode(request.getPassword()));
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return new JwtTokenResponse(jwtToken);
    }

    public JwtTokenResponse authenticate(AuthenticationRequest request) {
        var user = repository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return new JwtTokenResponse(jwtToken);
    }
}
