package com.onlinePharmacy.order.service;

import com.onlinePharmacy.order.dto.CartItemRequest;
import com.onlinePharmacy.order.dto.CartResponse;
import com.onlinePharmacy.order.dto.DeleteResponse;

public interface CartService {
    CartResponse getCart(Long userId);
    CartResponse addOrUpdateItem(Long userId, CartItemRequest request);
    CartResponse updateItemQuantity(Long userId, Long medicineId, Integer quantity);
    DeleteResponse removeItem(Long userId, Long medicineId);
    void clearCart(Long userId);
}
