package com.onlinePharmacy.order.client;

import org.springframework.stereotype.Component;

import com.onlinePharmacy.order.dto.UserResponse;

@Component
public class UserClientFallback implements UserClient {
    @Override
    public UserResponse getUserById(Long id) {
        // Return a dummy object so the order can still proceed
        UserResponse fallbackUser = new UserResponse();
        fallbackUser.setEmail("customer@pharmacy.com"); 
        fallbackUser.setName("Valued Customer");
        return fallbackUser;
    }
}