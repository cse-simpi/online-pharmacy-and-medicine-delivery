package com.onlinePharmacy.catalog.repository;

import com.onlinePharmacy.catalog.entity.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    // Search by name (partial, case-insensitive)
    Page<Medicine> findByNameContainingIgnoreCaseAndActiveTrue(String name, Pageable pageable);

    // Filter by category
    Page<Medicine> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);

    // Filter by requires prescription
    Page<Medicine> findByRequiresPrescriptionAndActiveTrue(Boolean requiresPrescription, Pageable pageable);
    
 // Check if medicine already exists by Name and Manufacturer
    Optional<Medicine> findByNameIgnoreCaseAndManufacturerIgnoreCaseAndGenericNameIgnoreCase(String name, String manufacturer,String genericName);
    
    // Combined search + filter
    @Query("""
    	    SELECT m FROM Medicine m
    	    WHERE m.active = true
    	      AND (:name IS NULL OR 
    	           LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')) OR 
    	           LOWER(m.genericName) LIKE LOWER(CONCAT('%', :name, '%')))
    	      AND (:categoryId IS NULL OR m.category.id = :categoryId)
    	      AND (:requiresPrescription IS NULL OR m.requiresPrescription = :requiresPrescription)
    	      AND (:minPrice IS NULL OR m.price >= :minPrice)
    	      AND (:maxPrice IS NULL OR m.price <= :maxPrice)
    	""")
    	Page<Medicine> searchMedicines(
    	        @Param("name") String name,
    	        @Param("categoryId") Long categoryId,
    	        @Param("requiresPrescription") Boolean requiresPrescription,
    	        @Param("minPrice") Double minPrice,
    	        @Param("maxPrice") Double maxPrice,
    	        Pageable pageable
    	);

    // Low stock medicines (for admin dashboard)
    @Query("SELECT m FROM Medicine m WHERE m.active = true AND m.stock <= :threshold")
    List<Medicine> findLowStockMedicines(@Param("threshold") int threshold);

    // Expiring medicines (for admin dashboard)
    @Query("SELECT m FROM Medicine m WHERE m.active = true AND m.expiryDate <= :date")
    List<Medicine> findExpiringMedicines(@Param("date") LocalDate date);

 // Find all active medicines with the same generic name
    List<Medicine> findByGenericNameIgnoreCaseAndActiveTrue(String genericName);
    
    // Check if medicine exists in category
    boolean existsByCategoryIdAndActiveTrue(Long categoryId);
}