package com.onlinePharmacy.auth.controller;

import com.onlinePharmacy.auth.dto.UserResponse;

import com.onlinePharmacy.auth.service.UserService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}