package com.editorial.platform.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.editorial.platform.category.api.dto.CategoryRequest;
import com.editorial.platform.category.api.dto.CategoryResponse;
import com.editorial.platform.category.model.Category;
import com.editorial.platform.category.repository.CategoryRepository;
import com.editorial.platform.common.exception.BadRequestException;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(categoryRepository);
    }

    @Test
    void createCategoryShouldReturnCreatedCategory() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Mobility");
        request.setDescription("Warm-up and recovery content");

        Category savedCategory = new Category();
        savedCategory.setId(5L);
        savedCategory.setName("Mobility");
        savedCategory.setDescription("Warm-up and recovery content");

        when(categoryRepository.findByNameIgnoreCase("Mobility")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryResponse response = categoryService.createCategory(request);

        assertThat(response.getId()).isEqualTo(5L);
        assertThat(response.getName()).isEqualTo("Mobility");
    }

    @Test
    void createCategoryShouldFailWhenNameAlreadyExists() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Strength");

        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Strength");

        when(categoryRepository.findByNameIgnoreCase("Strength")).thenReturn(Optional.of(existingCategory));

        assertThatThrownBy(() -> categoryService.createCategory(request))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Category name already exists");
    }
}
