package com.onlinePharmacy.catalog.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.onlinePharmacy.catalog.dto.CategoryRequest;
import com.onlinePharmacy.catalog.dto.CategoryResponse;
import com.onlinePharmacy.catalog.dto.DeleteResponse;
import com.onlinePharmacy.catalog.entity.Category;
import com.onlinePharmacy.catalog.exception.DuplicateResourceException;
import com.onlinePharmacy.catalog.exception.ResourceNotFoundException;
import com.onlinePharmacy.catalog.repository.CategoryRepository;
import com.onlinePharmacy.catalog.util.CatalogMapper;

import jakarta.transaction.Transactional;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}
	
	//create category
	@Transactional
	public CategoryResponse createCategory(CategoryRequest request) {
		if(categoryRepository.existsByNameIgnoreCase(request.getName())) {
			throw new DuplicateResourceException("Category already exists: " + request.getName());
		}
		
		Category category = new Category(request.getName(), request.getDescription());
		return CatalogMapper.toCategoryResponse(categoryRepository.save(category));
		
	}
	
	//active categories
	public List<CategoryResponse> getAllActiveCategories(){
		return categoryRepository.findByActiveTrue()
				.stream().map(CatalogMapper::toCategoryResponse).collect(Collectors.toList());
	}
	
	//search by id
	public CategoryResponse getCategoryById(Long id) {
		return CatalogMapper.toCategoryResponse(categoryRepository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Category not found: " + id)));
	}
	
	//update category
	@Transactional
	public CategoryResponse updateCategory(Long id, CategoryRequest request) {
		Category category = categoryRepository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Category not found: " + id));
		
		category.setName(request.getName());
		category.setDescription(request.getDescription());

		return CatalogMapper.toCategoryResponse(categoryRepository.save(category));
	}
	
	//delete category
	@Transactional
	public DeleteResponse deleteCategory(Long id) {
		Category category = categoryRepository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Category not found: " + id));
		
		
		categoryRepository.delete(category);;
		
		return new DeleteResponse("Category with ID " + id + " has been deleted successfully");
		
	}
	
}
