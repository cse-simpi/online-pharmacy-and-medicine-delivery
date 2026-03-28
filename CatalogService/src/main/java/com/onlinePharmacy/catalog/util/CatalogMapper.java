package com.onlinePharmacy.catalog.util;



import com.onlinePharmacy.catalog.dto.CategoryResponse;
import com.onlinePharmacy.catalog.dto.MedicineResponse;
import com.onlinePharmacy.catalog.dto.PrescriptionResponse;
import com.onlinePharmacy.catalog.entity.Category;
import com.onlinePharmacy.catalog.entity.Medicine;
import com.onlinePharmacy.catalog.entity.Prescription;

public class CatalogMapper {

    private CatalogMapper() {}

    public static MedicineResponse toMedicineResponse(Medicine m) {
        MedicineResponse dto = new MedicineResponse();
        dto.setId(m.getId());
        dto.setName(m.getName());
        dto.setDescription(m.getDescription());
        dto.setManufacturer(m.getManufacturer());
        dto.setPrice(m.getPrice());
        dto.setDiscountedPrice(m.getDiscountedPrice());
        dto.setStock(m.getStock());
        dto.setRequiresPrescription(m.getRequiresPrescription());
        dto.setExpiryDate(m.getExpiryDate());
        dto.setDosageNotes(m.getDosageNotes());
        dto.setGenericName(m.getGenericName());
        dto.setActive(m.getActive());
        dto.setCreatedAt(m.getCreatedAt()); 
        dto.setUpdatedAt(m.getUpdatedAt());

        if (m.getCategory() != null) {
            dto.setCategoryId(m.getCategory().getId());
            dto.setCategoryName(m.getCategory().getName());
        }
        return dto;
    }

    public static CategoryResponse toCategoryResponse(Category c) {
        CategoryResponse dto = new CategoryResponse();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setDescription(c.getDescription());
        dto.setActive(c.getActive());
        dto.setCreatedAt(c.getCreatedAt()); 
        dto.setUpdatedAt(c.getUpdatedAt());
        return dto;
    }

    public static PrescriptionResponse toPrescriptionResponse(Prescription p) {
        PrescriptionResponse dto = new PrescriptionResponse();
        dto.setId(p.getId());
        dto.setUserId(p.getUserId());
        dto.setMedicineId(p.getMedicineId());
        dto.setFileName(p.getFileName());
        dto.setFileType(p.getFileType());
        dto.setStatus(p.getStatus().name());
        dto.setRejectionReason(p.getRejectionReason());
        dto.setVerifiedBy(p.getVerifiedBy());

      
        return dto;
    }
}