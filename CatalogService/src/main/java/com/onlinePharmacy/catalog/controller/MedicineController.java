package com.onlinePharmacy.catalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.onlinePharmacy.catalog.dto.DeleteResponse;
import com.onlinePharmacy.catalog.dto.MedicineRequest;
import com.onlinePharmacy.catalog.dto.MedicineResponse;
import com.onlinePharmacy.catalog.services.MedicineService;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/medicines")
@Tag(name = "Medicines", description = "Medicine catalog endpoints")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping
    @Operation(summary = "Search and filter medicines")
    public ResponseEntity<Page<MedicineResponse>> searchMedicines(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean requiresPrescription,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(medicineService.searchMedicines(
                name, categoryId, requiresPrescription, minPrice, maxPrice, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get medicine details by ID")
    public ResponseEntity<MedicineResponse> getMedicineById(@PathVariable Long id) {
        return ResponseEntity.ok(medicineService.getMedicineById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new medicine (Admin only)")
    public ResponseEntity<MedicineResponse> createMedicine(@Valid @RequestBody MedicineRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicineService.createMedicine(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update medicine (Admin only)")
    public ResponseEntity<MedicineResponse> updateMedicine(
            @PathVariable Long id, @Valid @RequestBody MedicineRequest request) {
        return ResponseEntity.ok(medicineService.updateMedicine(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "delete medicine (Admin only)")
    public ResponseEntity<DeleteResponse> deleteMedicine(@PathVariable Long id) {
        DeleteResponse response = medicineService.deleteMedicine(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get low stock medicines (Admin only)")
    public ResponseEntity<List<MedicineResponse>> getLowStock(
            @RequestParam(defaultValue = "10") int threshold) {
        return ResponseEntity.ok(medicineService.getLowStockMedicines(threshold));
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get expiring medicines within N days (Admin only)")
    public ResponseEntity<List<MedicineResponse>> getExpiring(
            @RequestParam(defaultValue = "30") int daysAhead) {
        return ResponseEntity.ok(medicineService.getExpiringMedicines(daysAhead));
    }
    
    @GetMapping("/alternatives")
    public ResponseEntity<List<MedicineResponse>> getAlternatives(
            @RequestParam("genericName") String genericName) {
        
        List<MedicineResponse> alternatives = medicineService.getAlternativesByGenericName(genericName);
        return ResponseEntity.ok(alternatives);
    }
}