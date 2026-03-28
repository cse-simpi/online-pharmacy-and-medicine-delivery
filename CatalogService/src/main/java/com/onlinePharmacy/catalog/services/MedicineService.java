package com.onlinePharmacy.catalog.services;

import com.onlinePharmacy.catalog.dto.DeleteResponse;
import com.onlinePharmacy.catalog.dto.MedicineRequest;
import com.onlinePharmacy.catalog.dto.MedicineResponse;
import com.onlinePharmacy.catalog.entity.Category;
import com.onlinePharmacy.catalog.entity.Medicine;
import com.onlinePharmacy.catalog.exception.ResourceNotFoundException;
import com.onlinePharmacy.catalog.repository.CategoryRepository;
import com.onlinePharmacy.catalog.repository.MedicineRepository;
import com.onlinePharmacy.catalog.util.CatalogMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final CategoryRepository categoryRepository;

    public MedicineService(MedicineRepository medicineRepository,
                               CategoryRepository categoryRepository) {
        this.medicineRepository = medicineRepository;
        this.categoryRepository = categoryRepository;
    }

   
    @Transactional
    public MedicineResponse createMedicine(MedicineRequest request) {
    	
    	Optional<Medicine> existingMedicine = medicineRepository
                .findByNameIgnoreCaseAndManufacturerIgnoreCaseAndGenericNameIgnoreCase(request.getName(), request.getManufacturer(), request.getGenericName());
    	
        if(existingMedicine.isPresent()) {
        	Medicine medicine = existingMedicine.get();
        	medicine.setStock(medicine.getStock()+ request.getStock());
        	
        	return CatalogMapper.toMedicineResponse(medicineRepository.save(medicine));
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.getCategoryId()));

        Medicine medicine = new Medicine();
        mapRequestToEntity(request, medicine, category);

        return CatalogMapper.toMedicineResponse(medicineRepository.save(medicine));
    }

    
    public MedicineResponse getMedicineById(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found: " + id));
        return CatalogMapper.toMedicineResponse(medicine);
    }

    
    public Page<MedicineResponse> searchMedicines(String name, Long categoryId,
                                                   Boolean requiresPrescription,
                                                   Double minPrice, Double maxPrice,
                                                   Pageable pageable) {
        return medicineRepository.searchMedicines(name, categoryId, requiresPrescription,
                minPrice, maxPrice, pageable)
                .map(CatalogMapper::toMedicineResponse);
    }

    
    @Transactional
    public MedicineResponse updateMedicine(Long id, MedicineRequest request) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.getCategoryId()));

        mapRequestToEntity(request, medicine, category);
        return CatalogMapper.toMedicineResponse(medicineRepository.save(medicine));
    }

    
    @Transactional
    public DeleteResponse deleteMedicine(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with ID: " + id));
        
        
        medicineRepository.delete(medicine); 
        
        return new DeleteResponse("Medicine with ID " + id + " has been deleted successfully.");
    }

    
    public List<MedicineResponse> getLowStockMedicines(int threshold) {
        return medicineRepository.findLowStockMedicines(threshold)
                .stream().map(CatalogMapper::toMedicineResponse).collect(Collectors.toList());
    }

   
    public List<MedicineResponse> getExpiringMedicines(int daysAhead) {
        LocalDate targetDate = LocalDate.now().plusDays(daysAhead);
        return medicineRepository.findExpiringMedicines(targetDate)
                .stream().map(CatalogMapper::toMedicineResponse).collect(Collectors.toList());
    }
    
    
    public List<MedicineResponse> getAlternativesByGenericName(String genericName) {
        // 1. Fetch from repository
        List<Medicine> medicines = medicineRepository.findByGenericNameIgnoreCaseAndActiveTrue(genericName);
        
        // 2. Map Entities to DTOs
        return medicines.stream()
                .map(CatalogMapper::toMedicineResponse)
                .collect(Collectors.toList());
    }

    private void mapRequestToEntity(MedicineRequest req, Medicine m, Category category) {
        m.setName(req.getName());
        m.setDescription(req.getDescription());
        m.setManufacturer(req.getManufacturer());
        m.setPrice(req.getPrice());
        m.setDiscountedPrice(req.getDiscountedPrice());
        m.setStock(req.getStock());
        m.setRequiresPrescription(req.getRequiresPrescription() != null && req.getRequiresPrescription());
        m.setExpiryDate(req.getExpiryDate());
        m.setDosageNotes(req.getDosageNotes());
        m.setCategory(category);
        m.setGenericName(req.getGenericName());
    }
}