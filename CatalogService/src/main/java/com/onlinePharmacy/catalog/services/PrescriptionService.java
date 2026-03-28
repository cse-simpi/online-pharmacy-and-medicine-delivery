package com.onlinePharmacy.catalog.services;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.onlinePharmacy.catalog.dto.PrescriptionResponse;
import com.onlinePharmacy.catalog.entity.Prescription;
import com.onlinePharmacy.catalog.entity.PrescriptionStatus;
import com.onlinePharmacy.catalog.exception.InvalidFileException;
import com.onlinePharmacy.catalog.exception.ResourceNotFoundException;
import com.onlinePharmacy.catalog.repository.PrescriptionRepository;
import com.onlinePharmacy.catalog.util.CatalogMapper;

import java.io.IOException;
import java.nio.file.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PrescriptionService {

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/png", "application/pdf"
    );
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Value("${prescription.upload.dir:uploads/prescriptions}")
    private String uploadDir;

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

  
    @Transactional
    public PrescriptionResponse uploadPrescription(Long userId, Long medicineId, MultipartFile file) {
        // Validate file
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File is empty or missing");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new InvalidFileException("Only PDF, JPG, and PNG files are allowed");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException("File size must not exceed 5MB");
        }

        // Save file to disk
        String savedPath = saveFile(file, userId);

        Prescription prescription = new Prescription();
        prescription.setUserId(userId);
        prescription.setMedicineId(medicineId);
        prescription.setFileName(file.getOriginalFilename());
        prescription.setFilePath(savedPath);
        prescription.setFileType(file.getContentType());

        return CatalogMapper.toPrescriptionResponse(prescriptionRepository.save(prescription));
    }

    
    public List<PrescriptionResponse> getUserPrescriptions(Long userId) {
        return prescriptionRepository.findByUserId(userId)
                .stream().map(CatalogMapper::toPrescriptionResponse).collect(Collectors.toList());
    }

    
    public PrescriptionResponse getPrescriptionById(Long id, Long userId) {
        Prescription prescription = prescriptionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found: " + id));
        return CatalogMapper.toPrescriptionResponse(prescription);
    }

    
    public Page<PrescriptionResponse> getPrescriptionsByStatus(PrescriptionStatus status, Pageable pageable) {
        return prescriptionRepository.findByStatus(status, pageable)
                .map(CatalogMapper::toPrescriptionResponse);
    }

    
    @Transactional
    public PrescriptionResponse updatePrescriptionStatus(Long id, PrescriptionStatus status,
                                                          String rejectionReason, Long verifiedBy) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found: " + id));
        prescription.setStatus(status);
        prescription.setVerifiedBy(verifiedBy);
        
        if (status == PrescriptionStatus.REJECTED) {
            prescription.setRejectionReason(rejectionReason);
        }
        return CatalogMapper.toPrescriptionResponse(prescriptionRepository.save(prescription));
    }

   
    public long countPendingPrescriptions() {
        return prescriptionRepository.countByStatus(PrescriptionStatus.PENDING);
    }

    private String saveFile(MultipartFile file, Long userId) {
        try {
            Path dir = Paths.get(uploadDir, String.valueOf(userId));
            Files.createDirectories(dir);
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return target.toString();
        } catch (IOException e) {
            throw new InvalidFileException("Failed to store file: " + e.getMessage());
        }
    }
}