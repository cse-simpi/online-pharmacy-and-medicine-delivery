package com.onlinePharmacy.catalog.service;


import com.onlinePharmacy.catalog.dto.MedicineRequest;
import com.onlinePharmacy.catalog.dto.MedicineResponse;
import com.onlinePharmacy.catalog.entity.Category;
import com.onlinePharmacy.catalog.entity.Medicine;
import com.onlinePharmacy.catalog.exception.ResourceNotFoundException;
import com.onlinePharmacy.catalog.repository.CategoryRepository;
import com.onlinePharmacy.catalog.repository.MedicineRepository;
import com.onlinePharmacy.catalog.services.MedicineService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicineServiceTest {

    @Mock
    private MedicineRepository medicineRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private MedicineService medicineService;

    private Category category;
    private Medicine medicine;
    private MedicineRequest request;

    @BeforeEach
    void setUp() {
        category = new Category("Antibiotics", "Antibiotic medicines");

        medicine = new Medicine();
        medicine.setName("Amoxicillin");
        medicine.setManufacturer("ABC Pharma");
        medicine.setPrice(120.0);
        medicine.setStock(100);
        medicine.setRequiresPrescription(true);
        medicine.setCategory(category);

        request = new MedicineRequest();
        request.setName("Amoxicillin");
        request.setManufacturer("ABC Pharma");
        request.setPrice(120.0);
        request.setStock(100);
        request.setRequiresPrescription(true);
        request.setCategoryId(1L);
    }

    @Test
    void createMedicine_success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

        MedicineResponse response = medicineService.createMedicine(request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Amoxicillin");
        verify(medicineRepository, times(1)).save(any(Medicine.class));
    }

    @Test
    void createMedicine_categoryNotFound_throwsException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> medicineService.createMedicine(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found");

        verify(medicineRepository, never()).save(any());
    }

    @Test
    void getMedicineById_success() {
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));

        MedicineResponse response = medicineService.getMedicineById(1L);

        assertThat(response.getName()).isEqualTo("Amoxicillin");
        assertThat(response.getRequiresPrescription()).isTrue();
    }

    @Test
    void getMedicineById_notFound_throwsException() {
        when(medicineRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> medicineService.getMedicineById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Medicine not found");
    }

    @Test
    void deleteMedicine_softDelete_success() {
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));
        when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

        medicineService.deleteMedicine(1L);

        assertThat(medicine.getActive()).isFalse();
        verify(medicineRepository, times(1)).save(medicine);
    }

   
}