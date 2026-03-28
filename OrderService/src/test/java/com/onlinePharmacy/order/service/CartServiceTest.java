package com.onlinePharmacy.order.service;


import com.onlinePharmacy.order.dto.CartItemRequest;
import com.onlinePharmacy.order.dto.CartResponse;
import com.onlinePharmacy.order.entity.CartItem;
import com.onlinePharmacy.order.exception.BadRequestException;
import com.onlinePharmacy.order.repository.CartItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private CartItem cartItem;
    private CartItemRequest request;

    @BeforeEach
    void setUp() {
        cartItem = new CartItem();
        cartItem.setUserId(1L);
        cartItem.setMedicineId(10L);
        cartItem.setMedicineName("Paracetamol");
        cartItem.setPrice(50.0);
        cartItem.setQuantity(2);
        cartItem.setRequiresPrescription(false);

        request = new CartItemRequest();
        request.setMedicineId(10L);
     
        request.setQuantity(2);
      
    }

    @Test
    void getCart_returnsCartWithCorrectTotals() {
        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));

        CartResponse response = cartService.getCart(1L);

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getSubTotal()).isEqualTo(100.0);           // 50 * 2
        assertThat(response.getDeliveryCharge()).isEqualTo(49.0);      // below free threshold
        assertThat(response.getTotalAmount()).isEqualTo(149.0);
        assertThat(response.isHasPrescriptionItems()).isFalse();
    }

    @Test
    void getCart_aboveFreeDeliveryThreshold_noDeliveryCharge() {
        cartItem.setPrice(300.0);
        cartItem.setQuantity(2); // total = 600 > 499 threshold
        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));

        CartResponse response = cartService.getCart(1L);

        assertThat(response.getDeliveryCharge()).isEqualTo(0.0);
        assertThat(response.getTotalAmount()).isEqualTo(600.0);
    }



    @Test
    void updateItemQuantity_zeroQuantity_deletesItem() {
        when(cartItemRepository.findByUserIdAndMedicineId(1L, 10L)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of());

        cartService.updateItemQuantity(1L, 10L, 0);

        verify(cartItemRepository, times(1)).delete(cartItem);
    }

   

    @Test
    void removeItem_callsDeleteOnRepository() {
        cartService.removeItem(1L, 10L);
        verify(cartItemRepository, times(1)).deleteByUserIdAndMedicineId(1L, 10L);
    }

    @Test
    void clearCart_callsDeleteAllForUser() {
        cartService.clearCart(1L);
        verify(cartItemRepository, times(1)).deleteByUserId(1L);
    }
}