package com.supportdesk.security;

import com.supportdesk.user.UserResponse;

public record LoginResponse(
        String token,
        UserResponse user,
        boolean demo
) {
}