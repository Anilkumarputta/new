package com.editorial.platform.category.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.editorial.platform.category.api.dto.CategoryRequest;
import com.editorial.platform.category.api.dto.CategoryResponse;
import com.editorial.platform.category.model.Category;
import com.editorial.platform.category.repository.CategoryRepository;
import com.editorial.platform.common.exception.BadRequestException;
import com.editorial.platform.common.exception.ResourceNotFoundException;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        return toResponse(findCategory(id));
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        validateUniqueName(request.getName(), null);

        Category category = new Category();
        category.setName(request.getName().trim());
        category.setDescription(normalizeDescription(request.getDescription()));

        return toResponse(categoryRepository.save(category));
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = findCategory(id);
        validateUniqueName(request.getName(), id);

        category.setName(request.getName().trim());
        category.setDescription(normalizeDescription(request.getDescription()));

        return toResponse(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        Category category = findCategory(id);
        categoryRepository.delete(category);
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID " + id));
    }

    private void validateUniqueName(String name, Long currentCategoryId) {
        categoryRepository.findByNameIgnoreCase(name.trim())
            .ifPresent(existingCategory -> {
                if (currentCategoryId == null || !existingCategory.getId().equals(currentCategoryId)) {
                    throw new BadRequestException("Category name already exists");
                }
            });
    }

    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        return description.trim();
    }

    private CategoryResponse toResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        return response;
    }
}
