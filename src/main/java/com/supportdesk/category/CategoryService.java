package com.supportdesk.category;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class CategoryService {

        private final CategoryRepository categoryRepository;

        public CategoryService(
                        CategoryRepository categoryRepository) {
                this.categoryRepository = categoryRepository;
        }

        public List<CategoryResponse> getAllCategories() {
                return categoryRepository.findAll()
                                .stream()
                                .map(CategoryResponse::from)
                                .toList();
        }

        public CategoryResponse createCategory(String name) {

                String cleanName = name.trim();

                if (categoryRepository.existsByNameIgnoreCase(cleanName)) {
                        throw new IllegalArgumentException(
                                        "A category with this name already exists.");
                }

                Category category = new Category();

                category.setName(cleanName);
                category.setActive(true);
                category.setCreatedAt(OffsetDateTime.now());

                Category savedCategory = categoryRepository.save(category);

                return CategoryResponse.from(savedCategory);
        }

        public CategoryResponse updateCategory(
                        Long categoryId,
                        UpdateCategoryRequest request) {
                Category category = categoryRepository
                                .findById(categoryId)
                                .orElseThrow(() -> new com.supportdesk.exception.ResourceNotFoundException(
                                                "Category was not found."));

                category.setActive(request.active());

                Category savedCategory = categoryRepository.save(category);

                return CategoryResponse.from(savedCategory);
        }

}