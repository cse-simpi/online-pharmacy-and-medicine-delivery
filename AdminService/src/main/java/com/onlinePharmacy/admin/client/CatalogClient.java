package com.onlinePharmacy.admin.client;

import com.onlinePharmacy.admin.dto.CategoryDto;
import com.onlinePharmacy.admin.dto.MedicineDto;
import com.onlinePharmacy.admin.dto.PrescriptionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(
    name     = "catalog-service",
    fallback = CatalogClientFallback.class
)
public interface CatalogClient {

    // ── Medicine CRUD ─────────────────────────────────────────────────────────

    @GetMapping("/api/catalog/medicines")
    Map<String, Object> searchMedicines(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size);

    @GetMapping("/api/catalog/medicines/{id}")
    MedicineDto getMedicineById(@PathVariable("id") Long id);

    @PostMapping("/api/catalog/medicines")
    MedicineDto createMedicine(@RequestBody Object request);

    @PutMapping("/api/catalog/medicines/{id}")
    MedicineDto updateMedicine(@PathVariable("id") Long id, @RequestBody Object request);

    @DeleteMapping("/api/catalog/medicines/{id}")
    void deleteMedicine(@PathVariable("id") Long id);

    @GetMapping("/api/catalog/medicines/low-stock")
    List<MedicineDto> getLowStockMedicines(@RequestParam(defaultValue = "10") int threshold);

    @GetMapping("/api/catalog/medicines/expiring")
    List<MedicineDto> getExpiringMedicines(@RequestParam(defaultValue = "30") int daysAhead);

    // ── Category CRUD ─────────────────────────────────────────────────────────

    @GetMapping("/api/catalog/categories")
    List<CategoryDto> getAllCategories();

    @PostMapping("/api/catalog/categories")
    CategoryDto createCategory(@RequestBody Object request);

    @PutMapping("/api/catalog/categories/{id}")
    CategoryDto updateCategory(@PathVariable("id") Long id, @RequestBody Object request);

    @DeleteMapping("/api/catalog/categories/{id}")
    void deleteCategory(@PathVariable("id") Long id);

    // ── Prescription Management ───────────────────────────────────────────────

    @GetMapping("/api/catalog/prescriptions")
    Map<String, Object> getPrescriptionsByStatus(
            @RequestParam(defaultValue = "PENDING") String status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size);

    @PatchMapping("/api/catalog/prescriptions/{id}/status")
    PrescriptionDto updatePrescriptionStatus(
            @PathVariable("id") Long id,
            @RequestParam String status,
            @RequestParam(required = false) String rejectionReason,
            @RequestHeader("X-User-Id") String adminId);

    @GetMapping("/api/catalog/prescriptions/pending-count")
    Map<String, Long> getPendingPrescriptionCount();
}