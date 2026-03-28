package com.onlinePharmacy.auth.service;

import com.onlinePharmacy.auth.dto.UserResponse;
import com.onlinePharmacy.auth.exception.ResourceNotFoundException;
import com.onlinePharmacy.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail()
                ))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}