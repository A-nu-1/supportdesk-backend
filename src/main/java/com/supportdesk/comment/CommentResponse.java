package com.supportdesk.comment;

import java.time.OffsetDateTime;

public record CommentResponse(
        Long id,
        Long ticketId,
        Long userId,
        String userName,
        String message,
        OffsetDateTime createdAt
) {

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getTicket().getId(),
                comment.getUser().getId(),
                comment.getUser().getFirstName()
                        + " "
                        + comment.getUser().getLastName(),
                comment.getMessage(),
                comment.getCreatedAt()
        );
    }
}