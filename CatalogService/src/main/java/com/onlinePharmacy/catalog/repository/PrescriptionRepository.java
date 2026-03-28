package com.onlinePharmacy.catalog.repository;

import com.onlinePharmacy.catalog.entity.Prescription;
import com.onlinePharmacy.catalog.entity.PrescriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByUserId(Long userId);
    
    Page<Prescription> findByStatus(PrescriptionStatus status, Pageable pageable);
    
    Optional<Prescription> findByIdAndUserId(Long id, Long userId);
    
    List<Prescription> findByUserIdAndStatus(Long userId, PrescriptionStatus status);
    
    long countByStatus(PrescriptionStatus status);
}