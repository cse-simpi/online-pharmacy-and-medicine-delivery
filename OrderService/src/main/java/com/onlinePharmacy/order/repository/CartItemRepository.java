package com.onlinePharmacy.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.onlinePharmacy.order.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);
    Optional<CartItem> findByUserIdAndMedicineId(Long userId, Long medicineId);
    void deleteByUserId(Long userId);
    void deleteByUserIdAndMedicineId(Long userId, Long medicineId);
}
 
