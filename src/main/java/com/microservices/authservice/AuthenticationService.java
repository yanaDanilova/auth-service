package com.microservices.authservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public void register(Credentials request) {
        User user = new User(request.getUsername(), passwordEncoder.encode(request.getPassword()));
        List<String> roles = new ArrayList<>();
        roles.add("user");
        user.setRoles(roles);
        repository.save(user);
    }

    public JwtTokenResponse authenticate(Credentials request) {
        var user = repository.findByUsername(request.getUsername())
                .orElseThrow();
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            var jwtToken = jwtService.generateToken(user);
            return new JwtTokenResponse(jwtToken);
        }
        throw new BadCredentialsException("Incorrect password");

    }
}
