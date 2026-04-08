package com.editorial.platform.show.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.editorial.platform.category.model.Category;
import com.editorial.platform.category.repository.CategoryRepository;
import com.editorial.platform.common.exception.ResourceNotFoundException;
import com.editorial.platform.common.model.PublishingStatus;
import com.editorial.platform.show.api.dto.ShowRequest;
import com.editorial.platform.show.api.dto.ShowResponse;
import com.editorial.platform.show.model.Show;
import com.editorial.platform.show.repository.ShowRepository;

@Service
@Transactional
public class ShowService {

    private final ShowRepository showRepository;
    private final CategoryRepository categoryRepository;

    public ShowService(ShowRepository showRepository, CategoryRepository categoryRepository) {
        this.showRepository = showRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<ShowResponse> getAllShows() {
        return showRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public ShowResponse getShowById(Long id) {
        return toResponse(findShow(id));
    }

    public ShowResponse createShow(ShowRequest request) {
        Category category = findCategory(request.getCategoryId());

        Show show = new Show();
        show.setTitle(request.getTitle().trim());
        show.setDescription(request.getDescription().trim());
        show.setCategory(category);
        show.setStatus(PublishingStatus.DRAFT);
        show.setPublished(false);

        return toResponse(showRepository.save(show));
    }

    public ShowResponse updateShow(Long id, ShowRequest request) {
        Show show = findShow(id);
        Category category = findCategory(request.getCategoryId());

        show.setTitle(request.getTitle().trim());
        show.setDescription(request.getDescription().trim());
        show.setCategory(category);

        return toResponse(showRepository.save(show));
    }

    public void deleteShow(Long id) {
        Show show = findShow(id);
        showRepository.delete(show);
    }

    private Show findShow(Long id) {
        return showRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Show not found with ID " + id));
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID " + categoryId));
    }

    private ShowResponse toResponse(Show show) {
        ShowResponse response = new ShowResponse();
        response.setId(show.getId());
        response.setTitle(show.getTitle());
        response.setDescription(show.getDescription());
        response.setCategoryId(show.getCategory().getId());
        response.setCategoryName(show.getCategory().getName());
        response.setStatus(show.getStatus().name());
        response.setPublished(show.isPublished());
        response.setCreatedAt(show.getCreatedAt());
        response.setUpdatedAt(show.getUpdatedAt());
        return response;
    }
}
