package com.onlinePharmacy.order.controller;


import com.onlinePharmacy.order.dto.CartItemRequest;
import com.onlinePharmacy.order.dto.CartResponse;
import com.onlinePharmacy.order.service.CartService;
import com.onlinePharmacy.order.service.CartServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders/cart")
@Tag(name = "Cart", description = "Shopping cart management")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @Operation(summary = "View current user's cart")
    public ResponseEntity<CartResponse> getCart(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping
    @Operation(summary = "Add item to cart — validates stock and price via catalog-service")
    public ResponseEntity<CartResponse> addToCart(
            @RequestHeader("X-User-Id")     Long userId,
            @RequestHeader("Authorization") String bearerToken,
            @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(
                ((CartServiceImpl) cartService)
                        .addOrUpdateItem(userId, request, bearerToken));
    }

    @PatchMapping("/{medicineId}")
    @Operation(summary = "Update quantity of a specific cart item (set 0 to remove)")
    public ResponseEntity<CartResponse> updateQuantity(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long medicineId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateItemQuantity(userId, medicineId, quantity));
    }

    @DeleteMapping("/{medicineId}")
    @Operation(summary = "Remove a specific item from cart")
    public ResponseEntity<Void> removeItem(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long medicineId) {
        cartService.removeItem(userId, medicineId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "Clear all items from cart")
    public ResponseEntity<Void> clearCart(
            @RequestHeader("X-User-Id") Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
