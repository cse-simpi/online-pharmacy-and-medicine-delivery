package com.onlinePharmacy.order.client;

import com.onlinePharmacy.order.dto.MedicineClientResponse;
import com.onlinePharmacy.order.dto.PrescriptionClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
    name     = "catalog-service",
    fallback = CatalogClientFallback.class
)
public interface CatalogClient {

    /**
     * Fetch a medicine by ID.
     */
    @GetMapping("/api/catalog/medicines/{id}")
    MedicineClientResponse getMedicineById(@PathVariable("id") Long id);

    /**
     * Fetch all active medicines that share the same Generic Name (Salt).
     * Used for suggesting alternatives when a specific brand is out of stock.
     */
    @GetMapping("/api/catalog/medicines/alternatives")
    List<MedicineClientResponse> getAlternativesByGenericName(@RequestParam("genericName") String genericName);

    /**
     * Fetch a prescription by ID for the current user.
     */
    @GetMapping("/api/catalog/prescriptions/my/{id}")
    PrescriptionClientResponse getPrescriptionById(@PathVariable("id") Long prescriptionId);
}