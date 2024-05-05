package com.alkemy.taskmanager.security.rest;


import com.alkemy.taskmanager.security.core.AuthenticationService;
import com.alkemy.taskmanager.security.dto.LoginRequest;
import com.alkemy.taskmanager.security.dto.RegisterRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class SecurityController {

    private final AuthenticationService authenticationService;
    private static final String JWT_COOKIE_NAME = "jwt-token";
    private static final int JWT_EXPIRATION_MINUTES = 60;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            var token = authenticationService.login(loginRequest.user(), loginRequest.pass());
            addJwtToCookie(response, token);
            return ResponseEntity.ok().build();
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            var newUser = authenticationService.registerUser(registerRequest.user(), registerRequest.pass());
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody RegisterRequest registerAdminRequest) {
        try {
            var newAdmin = authenticationService.registerAdmin(registerAdminRequest.user(), registerAdminRequest.pass());
            return ResponseEntity.status(HttpStatus.CREATED).body(newAdmin);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private void addJwtToCookie(HttpServletResponse response, String token) {
        var cookie = new Cookie(JWT_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(JWT_EXPIRATION_MINUTES * 60);
        response.addCookie(cookie);
    }

}
