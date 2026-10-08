package com.supportdesk.security;

import com.supportdesk.user.Role;
import jakarta.validation.constraints.NotNull;

public record DemoLoginRequest(

        @NotNull
        Role role

) {}