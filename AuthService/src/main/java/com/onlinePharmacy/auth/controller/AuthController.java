package com.onlinePharmacy.auth.controller;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onlinePharmacy.auth.dto.AuthResponse;
import com.onlinePharmacy.auth.dto.LoginRequest;
import com.onlinePharmacy.auth.dto.SignupRequest;
import com.onlinePharmacy.auth.dto.SignupResponse;
import com.onlinePharmacy.auth.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}
	
	//register
	@PostMapping("/signup")
	public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest req){
		SignupResponse response = authService.signup(req);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	//login
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req){
		AuthResponse response = authService.login(req);
		return ResponseEntity.ok(response);
	}
	
	
	
}
