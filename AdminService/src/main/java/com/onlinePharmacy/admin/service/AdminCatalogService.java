package com.onlinePharmacy.admin.service;

import com.onlinePharmacy.admin.client.CatalogClient;
import com.onlinePharmacy.admin.dto.CategoryDto;
import com.onlinePharmacy.admin.dto.MedicineDto;
import com.onlinePharmacy.admin.entity.AdminAuditLog.AdminAction;
import com.onlinePharmacy.admin.exception.ServiceUnavailableException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdminCatalogService {

    private final CatalogClient   catalogClient;
    private final AuditLogService auditLogService;

    public AdminCatalogService(CatalogClient catalogClient,
                               AuditLogService auditLogService) {
        this.catalogClient   = catalogClient;
        this.auditLogService = auditLogService;
    }

    // ── Medicine ──────────────────────────────────────────────────────────────

    /**
     * GET /api/admin/medicines
     * Paginated medicine search with optional name/category filters.
     */
    public Map<String, Object> searchMedicines(String name, Long categoryId,
                                                int page, int size) {
        Map<String, Object> result = catalogClient.searchMedicines(name, categoryId, page, size);
        if (result == null) throw new ServiceUnavailableException("Catalog service unavailable.");
        return result;
    }

    /**
     * GET /api/admin/medicines/{id}
     */
    public MedicineDto getMedicineById(Long id) {
        MedicineDto medicine = catalogClient.getMedicineById(id);
        if (medicine == null) throw new ServiceUnavailableException("Catalog service unavailable.");
        return medicine;
    }

    /**
     * POST /api/admin/medicines
     * Creates a new medicine in catalog-service and logs the action.
     */
    public MedicineDto createMedicine(Object request,
                                      Long adminId, String adminEmail) {
        MedicineDto created = catalogClient.createMedicine(request);
        if (created == null) throw new ServiceUnavailableException("Catalog service unavailable.");

        auditLogService.log(
                adminId, adminEmail,
                AdminAction.MEDICINE_CREATED,
                "MEDICINE", created.getId(),
                "Medicine '" + created.getName() + "' created by " + adminEmail
        );
        return created;
    }

    /**
     * PUT /api/admin/medicines/{id}
     * Updates an existing medicine and logs the action.
     */
    public MedicineDto updateMedicine(Long id, Object request,
                                      Long adminId, String adminEmail) {
        MedicineDto updated = catalogClient.updateMedicine(id, request);
        if (updated == null) throw new ServiceUnavailableException("Catalog service unavailable.");

        auditLogService.log(
                adminId, adminEmail,
                AdminAction.MEDICINE_UPDATED,
                "MEDICINE", id,
                "Medicine #" + id + " '" + updated.getName() + "' updated by " + adminEmail
        );
        return updated;
    }

    /**
     * DELETE /api/admin/medicines/{id}
     * Soft-deletes medicine in catalog-service and logs the action.
     */
    public void deleteMedicine(Long id, Long adminId, String adminEmail) {
        catalogClient.deleteMedicine(id);
        auditLogService.log(
                adminId, adminEmail,
                AdminAction.MEDICINE_DELETED,
                "MEDICINE", id,
                "Medicine #" + id + " deleted by " + adminEmail
        );
    }

    /**
     * GET /api/admin/medicines/low-stock
     * Returns medicines below the given stock threshold (default 10).
     */
    public List<MedicineDto> getLowStockMedicines(int threshold) {
        return catalogClient.getLowStockMedicines(threshold);
    }

    /**
     * GET /api/admin/medicines/expiring
     * Returns medicines expiring within the given number of days (default 30).
     */
    public List<MedicineDto> getExpiringMedicines(int daysAhead) {
        return catalogClient.getExpiringMedicines(daysAhead);
    }

    // ── Category ──────────────────────────────────────────────────────────────

    /**
     * GET /api/admin/categories
     */
    public List<CategoryDto> getAllCategories() {
        return catalogClient.getAllCategories();
    }

    /**
     * POST /api/admin/categories
     */
    public CategoryDto createCategory(Object request,
                                      Long adminId, String adminEmail) {
        CategoryDto created = catalogClient.createCategory(request);
        if (created == null) throw new ServiceUnavailableException("Catalog service unavailable.");

        auditLogService.log(
                adminId, adminEmail,
                AdminAction.CATEGORY_CREATED,
                "CATEGORY", created.getId(),
                "Category '" + created.getName() + "' created by " + adminEmail
        );
        return created;
    }

    /**
     * PUT /api/admin/categories/{id}
     */
    public CategoryDto updateCategory(Long id, Object request,
                                      Long adminId, String adminEmail) {
        CategoryDto updated = catalogClient.updateCategory(id, request);
        if (updated == null) throw new ServiceUnavailableException("Catalog service unavailable.");

        auditLogService.log(
                adminId, adminEmail,
                AdminAction.CATEGORY_UPDATED,
                "CATEGORY", id,
                "Category #" + id + " updated by " + adminEmail
        );
        return updated;
    }

    /**
     * DELETE /api/admin/categories/{id}
     */
    public void deleteCategory(Long id, Long adminId, String adminEmail) {
        catalogClient.deleteCategory(id);
        auditLogService.log(
                adminId, adminEmail,
                AdminAction.CATEGORY_DELETED,
                "CATEGORY", id,
                "Category #" + id + " deleted by " + adminEmail
        );
    }
}