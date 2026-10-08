package com.supportdesk.category;

public record CategoryResponse(
        Long id,
        String name,
        boolean active
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.isActive()
        );
    }
}