package com.supportdesk.ticket;

import jakarta.validation.constraints.NotNull;

public record UpdateTicketPriorityRequest(
        @NotNull TicketPriority priority
) {}