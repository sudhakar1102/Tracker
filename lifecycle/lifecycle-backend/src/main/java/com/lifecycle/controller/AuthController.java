package com.lifecycle.controller;

import com.lifecycle.dto.Dtos.*;
import com.lifecycle.entity.User;
import com.lifecycle.repository.UserRepository;
import com.lifecycle.security.JwtUtil;
import com.lifecycle.utility.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController @RequestMapping("/api")
public class AuthController {
    private final UserRepository users; private final PasswordEncoder encoder; private final JwtUtil jwt;
    public AuthController(UserRepository u, PasswordEncoder e, JwtUtil j) { users = u; encoder = e; jwt = j; }

    @PostMapping("/auth/register")
    public AuthRes register(@Valid @RequestBody RegisterReq r) {
        String email = r.email().trim().toLowerCase();
        if (users.findByEmail(email).isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        User u = new User(); u.name = r.name(); u.email = email; u.phone = r.phone(); u.password = encoder.encode(r.password());
        users.save(u);
        return new AuthRes(jwt.generate(u.id), u.name);
    }

    @PostMapping("/auth/login")
    public AuthRes login(@Valid @RequestBody LoginReq r) {
        User u = users.findByEmail(r.email().trim().toLowerCase())
                .filter(x -> encoder.matches(r.password(), x.password))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect email or password"));
        return new AuthRes(jwt.generate(u.id), u.name);
    }

    @GetMapping("/users/profile")
    public User profile() { return users.findById(CurrentUser.id()).orElseThrow(); }
}
