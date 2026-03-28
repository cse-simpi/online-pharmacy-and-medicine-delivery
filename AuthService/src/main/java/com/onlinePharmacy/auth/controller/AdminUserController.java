package com.onlinePharmacy.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onlinePharmacy.auth.dto.SignupRequest;
import com.onlinePharmacy.auth.dto.SignupResponse;
import com.onlinePharmacy.auth.service.AuthService;


@RestController
@RequestMapping("/api/auth")
public class AdminUserController {

    private final AuthService authService;

    public AdminUserController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/create-staff")
    @PreAuthorize("hasRole('ADMIN')") // Only existing admins can touch this!
    public ResponseEntity<SignupResponse> createStaff(@RequestBody SignupRequest request) {
        // We pass 'true' or a specific role to indicate this is a staff creation
        SignupResponse response = authService.createStaff(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
