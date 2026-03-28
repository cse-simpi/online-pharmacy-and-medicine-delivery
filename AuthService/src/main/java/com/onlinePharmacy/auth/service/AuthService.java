package com.onlinePharmacy.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.onlinePharmacy.auth.dto.AuthResponse;
import com.onlinePharmacy.auth.dto.LoginRequest;
import com.onlinePharmacy.auth.dto.SignupRequest;
import com.onlinePharmacy.auth.dto.SignupResponse;
import com.onlinePharmacy.auth.entity.Role;
import com.onlinePharmacy.auth.entity.User;
import com.onlinePharmacy.auth.entity.UserStatus;
import com.onlinePharmacy.auth.exception.DuplicateResourceException;
import com.onlinePharmacy.auth.exception.InvalidCredentialsException;
import com.onlinePharmacy.auth.exception.UserInactiveException;
import com.onlinePharmacy.auth.repository.UserRepository;
import com.onlinePharmacy.auth.security.JwtUtil;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	
	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
	}
	
	
	public SignupResponse signup(SignupRequest request) {
		
		// check if already exists
		if(userRepository.existsByEmail(request.getEmail())) {
			throw new DuplicateResourceException("Email already exists " + request.getEmail());
		}
		
		if(userRepository.existsByMobile(request.getMobile())) {
			throw new DuplicateResourceException("Mobile number already exists " + request.getMobile());
		}
		
		//user
		User user = new User(
				request.getName(),
				request.getEmail(),
				request.getMobile(),
				passwordEncoder.encode(request.getPassword()),
				Role.CUSTOMER);
		
		User saved = userRepository.save(user);
		
		return new SignupResponse(
				saved.getId(),
				saved.getName(),
				saved.getEmail(),
				saved.getMobile(),
				saved.getRole().name());
		
	}
	
	//login user
	public AuthResponse login(LoginRequest request) {
		//check for email
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(()-> new InvalidCredentialsException("Invalid email or password"));
		
		//check for password
		if(!passwordEncoder.matches(request.getPassword(), user.getPassword()))
		{
			throw new InvalidCredentialsException("Invalid email or password");
		}
		// check for status
		if(user.getStatus() != UserStatus.ACTIVE) {
			throw new UserInactiveException("Account is " + user.getStatus().name().toLowerCase() + ".");
		}
	
		    String token = jwtUtil.generateToken(user);
		    return new AuthResponse(token,user.getRole().name(), user.getEmail(),user.getName());
		
		
	}
	
	public SignupResponse createStaff(SignupRequest request) {
		// check if already exists
				if(userRepository.existsByEmail(request.getEmail())) {
					throw new DuplicateResourceException("Email already exists " + request.getEmail());
				}
				
				if(userRepository.existsByMobile(request.getMobile())) {
					throw new DuplicateResourceException("Mobile number already exists " + request.getMobile());
				}
				
				User user = new User(
			            request.getName(),
			            request.getEmail(),
			            request.getMobile(),
			            passwordEncoder.encode(request.getPassword()),
			            Role.ADMIN
			        );

			   User saved = userRepository.save(user);
			   
			   return new SignupResponse(
						saved.getId(),
						saved.getName(),
						saved.getEmail(),
						saved.getMobile(),
						saved.getRole().name());
			   
	}
	
	
}
