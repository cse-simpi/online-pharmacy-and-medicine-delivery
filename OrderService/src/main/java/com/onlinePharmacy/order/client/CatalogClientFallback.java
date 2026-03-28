package com.onlinePharmacy.order.client;

import com.onlinePharmacy.order.dto.MedicineClientResponse;
import com.onlinePharmacy.order.dto.PrescriptionClientResponse;
import com.onlinePharmacy.order.exception.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Fallback implementation for CatalogClient.
 */
@Component
public class CatalogClientFallback implements CatalogClient {

    private static final Logger log = LoggerFactory.getLogger(CatalogClientFallback.class);

    @Override
    public MedicineClientResponse getMedicineById(Long medicineId) {
        log.error("CatalogClient fallback: getMedicineById({}) — catalog-service unavailable", medicineId);
        throw new ServiceUnavailableException(
                "Catalog service is currently unavailable. Cannot fetch medicine details.");
    }

    // --- ADDED THIS METHOD ---
    @Override
    public List<MedicineClientResponse> getAlternativesByGenericName(String genericName) {
        log.error("CatalogClient fallback: getAlternativesByGenericName({}) — catalog-service unavailable", genericName);
        // Instead of throwing an exception, return an empty list so the 
        // main Cart logic doesn't crash just because recommendations failed.
        return Collections.emptyList();
    }

    @Override
    public PrescriptionClientResponse getPrescriptionById(Long prescriptionId) {
        log.error("CatalogClient fallback: getPrescriptionById({}) — catalog-service unavailable", prescriptionId);
        throw new ServiceUnavailableException(
                "Catalog service is currently unavailable. Cannot verify prescription.");
    }
}