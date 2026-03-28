package com.onlinePharmacy.order.service;

import java.util.List;

import com.onlinePharmacy.order.dto.AddressRequest;
import com.onlinePharmacy.order.dto.AddressResponse;
import com.onlinePharmacy.order.dto.DeleteResponse;

public interface AddressService {
    AddressResponse addAddress(Long userId, AddressRequest request);
    List<AddressResponse> getUserAddresses(Long userId);
    AddressResponse getAddressById(Long userId, Long addressId);
    AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request);
    DeleteResponse deleteAddress(Long userId, Long addressId);
    AddressResponse setDefaultAddress(Long userId, Long addressId);
}