package com.onlinePharmacy.order.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onlinePharmacy.order.dto.AddressRequest;
import com.onlinePharmacy.order.dto.AddressResponse;
import com.onlinePharmacy.order.dto.DeleteResponse;
import com.onlinePharmacy.order.entity.Address;
import com.onlinePharmacy.order.exception.ResourceNotFoundException;
import com.onlinePharmacy.order.repository.AddressRepository;
import com.onlinePharmacy.order.util.OrderMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    @Transactional
    public AddressResponse addAddress(Long userId, AddressRequest request) {
        // If this is the first address or marked as default, clear other defaults
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            clearExistingDefault(userId);
        }
        Address address = mapRequestToEntity(request, new Address());
        address.setUserId(userId);

        // First address is automatically default
        List<Address> existing = addressRepository.findByUserId(userId);
        if (existing.isEmpty()) address.setIsDefault(true);

        return OrderMapper.toAddressResponse(addressRepository.save(address));
    }

    @Override
    public List<AddressResponse> getUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId)
                .stream().map(OrderMapper::toAddressResponse).collect(Collectors.toList());
    }

    @Override
    public AddressResponse getAddressById(Long userId, Long addressId) {
        return OrderMapper.toAddressResponse(findByIdAndUser(userId, addressId));
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
        Address address = findByIdAndUser(userId, addressId);
        if (Boolean.TRUE.equals(request.getIsDefault())) clearExistingDefault(userId);
        mapRequestToEntity(request, address);
        return OrderMapper.toAddressResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public DeleteResponse deleteAddress(Long userId, Long addressId) {
        Address address = findByIdAndUser(userId, addressId);
        addressRepository.delete(address);
        return new DeleteResponse("Address with user ID " + userId + "and addressId " + addressId +  " has been deleted successfully");
    }

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(Long userId, Long addressId) {
        clearExistingDefault(userId);
        Address address = findByIdAndUser(userId, addressId);
        address.setIsDefault(true);
        return OrderMapper.toAddressResponse(addressRepository.save(address));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Address findByIdAndUser(Long userId, Long addressId) {
        return addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + addressId));
    }

    private void clearExistingDefault(Long userId) {
        addressRepository.findByUserIdAndIsDefaultTrue(userId).ifPresent(a -> {
            a.setIsDefault(false);
            addressRepository.save(a);
        });
    }

    private Address mapRequestToEntity(AddressRequest req, Address address) {
        address.setFullName(req.getFullName());
        address.setPhone(req.getPhone());
        address.setAddressLine1(req.getAddressLine1());
        address.setAddressLine2(req.getAddressLine2());
        address.setCity(req.getCity());
        address.setState(req.getState());
        address.setPincode(req.getPincode());
        address.setCountry(req.getCountry() != null ? req.getCountry() : "India");
        address.setIsDefault(Boolean.TRUE.equals(req.getIsDefault()));
        return address;
    }
}