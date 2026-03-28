package com.onlinePharmacy.order.service;

import com.onlinePharmacy.order.client.CatalogClient;
import com.onlinePharmacy.order.dto.MedicineClientResponse;
import com.onlinePharmacy.order.dto.MedicineSuggestion;
import com.onlinePharmacy.order.dto.CartItemRequest;
import com.onlinePharmacy.order.dto.CartItemResponse;
import com.onlinePharmacy.order.dto.CartResponse;
import com.onlinePharmacy.order.dto.DeleteResponse;
import com.onlinePharmacy.order.entity.CartItem;
import com.onlinePharmacy.order.exception.BadRequestException;
import com.onlinePharmacy.order.exception.InsufficientStockException;
import com.onlinePharmacy.order.repository.CartItemRepository;
import com.onlinePharmacy.order.util.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private static final double DELIVERY_CHARGE     = 49.0;
    private static final double FREE_DELIVERY_ABOVE = 499.0;

    private final CartItemRepository cartItemRepository;
    private final CatalogClient      catalogClient;

    public CartServiceImpl(CartItemRepository cartItemRepository,
                           CatalogClient catalogClient) {
        this.cartItemRepository = cartItemRepository;
        this.catalogClient      = catalogClient;
    }

    @Override
    public CartResponse getCart(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        return buildCartResponse(items);
    }

    @Override
    @Transactional
    public CartResponse addOrUpdateItem(Long userId, CartItemRequest request) {
        throw new BadRequestException("Authorization token required for adding items to cart.");
    }

    @Transactional
    public CartResponse addOrUpdateItem(Long userId, CartItemRequest request, String bearerToken) {
        // 1. Fetch real medicine data via Feign (Now includes genericName)
        MedicineClientResponse medicine = catalogClient.getMedicineById(request.getMedicineId());

        // 2. Validate medicine is active
        if (!Boolean.TRUE.equals(medicine.getActive())) {
            throw new BadRequestException("Medicine '" + medicine.getName() + "' is no longer available.");
        }

        // 3. Validate sufficient stock
        // Note: In a production app, you might suggest alternatives here if stock < quantity
        if (medicine.getStock() < request.getQuantity()) {
        	List<MedicineClientResponse> alternatives = 
        	        catalogClient.getAlternativesByGenericName(medicine.getGenericName());

        	    
        	List<MedicineSuggestion> suggestions = alternatives.stream()
                    .filter(alt -> !alt.getId().equals(medicine.getId())) 
                    .filter(alt -> alt.getStock() > 0)                   
                    .map(alt -> {
                        MedicineSuggestion dto = new MedicineSuggestion();
                        dto.setId(alt.getId());
                        dto.setName(alt.getName());
                        dto.setManufacturer(alt.getManufacturer());
                        dto.setPrice(alt.getDiscountedPrice() != null ? alt.getDiscountedPrice() : alt.getPrice());
                        dto.setAvailableStock(alt.getStock());
                        return dto;
                    }).collect(Collectors.toList());

        	throw new InsufficientStockException(
        	        "Insufficient stock for '" + medicine.getName() + "'. Available: " + medicine.getStock(),
        	        suggestions
        	    );
        }

        // 4. Use server-side price (snapshot current price)
        double effectivePrice = medicine.getDiscountedPrice() != null
                ? medicine.getDiscountedPrice()
                : medicine.getPrice();

        // 5. Upsert cart item
        Optional<CartItem> existing = cartItemRepository.findByUserIdAndMedicineId(userId, request.getMedicineId());

        if (existing.isPresent()) {
            CartItem item = existing.get();
            int newQty = item.getQuantity() + request.getQuantity();
            
            if (medicine.getStock() < newQty) {
                throw new BadRequestException("Cannot add more. Total stock available: " + medicine.getStock());
            }
            
            item.setQuantity(newQty);
            item.setPrice(effectivePrice);
            
            // Sync current Generic Name and update substitution preference
            item.setGenericName(medicine.getGenericName());
            if (request.getSubstitutionAllowed() != null) {
                item.setSubstitutionAllowed(request.getSubstitutionAllowed());
            }
            
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setUserId(userId);
            item.setMedicineId(medicine.getId());
            item.setMedicineName(medicine.getName());
            
            // Capture Generic Name snapshot
            item.setGenericName(medicine.getGenericName()); 
            
            item.setPrice(effectivePrice);
            item.setQuantity(request.getQuantity());
            item.setRequiresPrescription(medicine.getRequiresPrescription());
            
            // Set user preference for brand substitution
            item.setSubstitutionAllowed(
                    request.getSubstitutionAllowed() != null && request.getSubstitutionAllowed());
            
            cartItemRepository.save(item);
        }

        return getCart(userId);
    }

    @Override
    @Transactional
    public CartResponse updateItemQuantity(Long userId, Long medicineId, Integer quantity) {
        CartItem item = cartItemRepository.findByUserIdAndMedicineId(userId, medicineId)
                .orElseThrow(() -> new BadRequestException("Item not found in cart"));
        
        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            // Re-validate stock if quantity increases
            MedicineClientResponse medicine = catalogClient.getMedicineById(medicineId);
            if (medicine.getStock() < quantity) {
                throw new BadRequestException("Insufficient stock. Available: " + medicine.getStock());
            }
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
        return getCart(userId);
    }

    @Override
    @Transactional
    public DeleteResponse removeItem(Long userId, Long medicineId) {
        cartItemRepository.deleteByUserIdAndMedicineId(userId, medicineId);
        return new DeleteResponse("Item removed from cart successfully");
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    private CartResponse buildCartResponse(List<CartItem> items) {
        List<CartItemResponse> itemDtos = items.stream()
                .map(OrderMapper::toCartItemResponse)
                .collect(Collectors.toList());

        double subTotal = items.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();

        double deliveryCharge = subTotal > 0 && subTotal < FREE_DELIVERY_ABOVE
                ? DELIVERY_CHARGE : 0.0;

        boolean hasPrescriptionItems = items.stream()
                .anyMatch(i -> Boolean.TRUE.equals(i.getRequiresPrescription()));

        CartResponse response = new CartResponse();
        response.setItems(itemDtos);
        response.setSubTotal(subTotal);
        response.setDeliveryCharge(deliveryCharge);
        response.setTotalAmount(subTotal + deliveryCharge);
        response.setItemCount(items.size());
        response.setHasPrescriptionItems(hasPrescriptionItems);
        return response;
    }
}