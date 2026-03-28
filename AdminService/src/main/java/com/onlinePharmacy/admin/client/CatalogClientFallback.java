package com.onlinePharmacy.admin.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.onlinePharmacy.admin.dto.CategoryDto;
import com.onlinePharmacy.admin.dto.MedicineDto;
import com.onlinePharmacy.admin.dto.PrescriptionDto;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class CatalogClientFallback implements CatalogClient {

    private static final Logger log = LoggerFactory.getLogger(CatalogClientFallback.class);

    @Override
    public Map<String, Object> searchMedicines(String name, Long categoryId,
            int page, int size) {
        log.error("CatalogClient fallback: searchMedicines");
        return Map.of("content", Collections.emptyList(), "totalElements", 0);
    }

    @Override
    public MedicineDto getMedicineById(Long id) {
        log.error("CatalogClient fallback: getMedicineById({})", id);
        return null;
    }

    @Override
    public MedicineDto createMedicine(Object request) {
        log.error("CatalogClient fallback: createMedicine");
        return null;
    }

    @Override
    public MedicineDto updateMedicine(Long id, Object request) {
        log.error("CatalogClient fallback: updateMedicine({})", id);
        return null;
    }

    @Override
    public void deleteMedicine(Long id) {
        log.error("CatalogClient fallback: deleteMedicine({})", id);
    }

    @Override
    public List<MedicineDto> getLowStockMedicines(int threshold) {
        log.error("CatalogClient fallback: getLowStockMedicines");
        return Collections.emptyList();
    }

    @Override
    public List<MedicineDto> getExpiringMedicines(int daysAhead) {
        log.error("CatalogClient fallback: getExpiringMedicines");
        return Collections.emptyList();
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        log.error("CatalogClient fallback: getAllCategories");
        return Collections.emptyList();
    }

    @Override
    public CategoryDto createCategory(Object request) {
        log.error("CatalogClient fallback: createCategory");
        return null;
    }

    @Override
    public CategoryDto updateCategory(Long id, Object request) {
        log.error("CatalogClient fallback: updateCategory({})", id);
        return null;
    }

    @Override
    public void deleteCategory(Long id) {
        log.error("CatalogClient fallback: deleteCategory({})", id);
    }

    @Override
    public Map<String, Object> getPrescriptionsByStatus(String status, int page,
            int size) {
        log.error("CatalogClient fallback: getPrescriptionsByStatus");
        return Map.of("content", Collections.emptyList(), "totalElements", 0);
    }

    @Override
    public PrescriptionDto updatePrescriptionStatus(Long id, String status,
            String rejectionReason,String adminId) {
        log.error("CatalogClient fallback: updatePrescriptionStatus({})", id);
        return null;
    }

    @Override
    public Map<String, Long> getPendingPrescriptionCount() {
        log.error("CatalogClient fallback: getPendingPrescriptionCount");
        return Map.of("pendingCount", -1L);
    }
}