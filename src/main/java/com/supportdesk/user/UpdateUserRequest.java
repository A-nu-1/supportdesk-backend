package com.supportdesk.user;

import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(
        @NotNull Role role,
        @NotNull Boolean active
) {}