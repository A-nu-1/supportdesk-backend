package com.supportdesk.category;

import jakarta.validation.constraints.NotNull;

public record UpdateCategoryRequest(
        @NotNull Boolean active
) {}