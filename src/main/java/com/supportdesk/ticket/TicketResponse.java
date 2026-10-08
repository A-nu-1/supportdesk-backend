package com.supportdesk.ticket;

import java.time.OffsetDateTime;

public record TicketResponse(
        Long id,
        String title,
        String description,
        Long categoryId,
        String categoryName,
        Long createdById,
        String createdByName,
        Long assignedToId,
        String assignedToName,
        TicketPriority priority,
        TicketStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static TicketResponse from(Ticket ticket) {

        Long assignedToId = null;
        String assignedToName = null;

        if (ticket.getAssignedTo() != null) {
            assignedToId = ticket.getAssignedTo().getId();
            assignedToName =
                    ticket.getAssignedTo().getFirstName()
                    + " "
                    + ticket.getAssignedTo().getLastName();
        }

        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),

                ticket.getCategory().getId(),
                ticket.getCategory().getName(),

                ticket.getCreatedBy().getId(),
                ticket.getCreatedBy().getFirstName()
                        + " "
                        + ticket.getCreatedBy().getLastName(),

                assignedToId,
                assignedToName,

                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }
}