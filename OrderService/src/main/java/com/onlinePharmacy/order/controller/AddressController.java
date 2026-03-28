package com.onlinePharmacy.order.controller;


import com.onlinePharmacy.order.dto.AddressRequest;
import com.onlinePharmacy.order.dto.AddressResponse;
import com.onlinePharmacy.order.dto.DeleteResponse;
import com.onlinePharmacy.order.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders/addresses")
@Tag(name = "Addresses", description = "Delivery address management")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    @Operation(summary = "Add a new delivery address")
    public ResponseEntity<AddressResponse> addAddress(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.addAddress(userId, request));
    }

    @GetMapping
    @Operation(summary = "List all addresses for current user")
    public ResponseEntity<List<AddressResponse>> getAddresses(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(addressService.getUserAddresses(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific address by ID")
    public ResponseEntity<AddressResponse> getAddressById(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(addressService.getAddressById(userId, id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing address")
    public ResponseEntity<AddressResponse> updateAddress(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.updateAddress(userId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an address")
    public ResponseEntity<DeleteResponse> deleteAddress(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        DeleteResponse response = addressService.deleteAddress(userId, id);
        return ResponseEntity.ok(response);

    }
    @PatchMapping("/{id}/set-default")
    @Operation(summary = "Set an address as the default delivery address")
    public ResponseEntity<AddressResponse> setDefault(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(addressService.setDefaultAddress(userId, id));
    }
}