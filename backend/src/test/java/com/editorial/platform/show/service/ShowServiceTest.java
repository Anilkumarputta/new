package com.editorial.platform.show.service;

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

import com.editorial.platform.audit.service.AuditLogService;
import com.editorial.platform.category.model.Category;
import com.editorial.platform.category.repository.CategoryRepository;
import com.editorial.platform.common.exception.ResourceNotFoundException;
import com.editorial.platform.common.model.PublishingStatus;
import com.editorial.platform.event.service.ContentEventPublisher;
import com.editorial.platform.show.api.dto.ShowRequest;
import com.editorial.platform.show.api.dto.ShowResponse;
import com.editorial.platform.show.model.Show;
import com.editorial.platform.show.repository.ShowRepository;

@ExtendWith(MockitoExtension.class)
class ShowServiceTest {

    @Mock
    private ShowRepository showRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private ContentEventPublisher contentEventPublisher;

    private ShowService showService;

    @BeforeEach
    void setUp() {
        showService = new ShowService(showRepository, categoryRepository, auditLogService, contentEventPublisher);
    }

    @Test
    void createShowShouldReturnCreatedShow() {
        ShowRequest request = new ShowRequest();
        request.setTitle("Core Strength");
        request.setDescription("Strength-focused show");
        request.setCategoryId(1L);

        Category category = new Category();
        category.setId(1L);
        category.setName("Strength");

        Show savedShow = new Show();
        savedShow.setId(10L);
        savedShow.setTitle(request.getTitle());
        savedShow.setDescription(request.getDescription());
        savedShow.setCategory(category);
        savedShow.setStatus(PublishingStatus.DRAFT);
        savedShow.setPublished(false);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(showRepository.save(any(Show.class))).thenReturn(savedShow);

        ShowResponse response = showService.createShow(request);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getCategoryName()).isEqualTo("Strength");
        assertThat(response.getStatus()).isEqualTo("DRAFT");
        assertThat(response.isPublished()).isFalse();
    }

    @Test
    void createShowShouldFailWhenCategoryDoesNotExist() {
        ShowRequest request = new ShowRequest();
        request.setTitle("Core Strength");
        request.setDescription("Strength-focused show");
        request.setCategoryId(99L);

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> showService.createShow(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Category not found");
    }
}
