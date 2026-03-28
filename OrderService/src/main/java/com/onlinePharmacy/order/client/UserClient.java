package com.onlinePharmacy.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.onlinePharmacy.order.dto.UserResponse;


@FeignClient(name = "auth-service", fallback = UserClientFallback.class)
public interface UserClient {
    @GetMapping("/api/users/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);
}