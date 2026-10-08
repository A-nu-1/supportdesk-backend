package com.supportdesk.comment;

import com.supportdesk.exception.ResourceNotFoundException;
import com.supportdesk.ticket.Ticket;
import com.supportdesk.ticket.TicketRepository;
import com.supportdesk.user.User;
import com.supportdesk.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import com.supportdesk.exception.ForbiddenOperationException;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public CommentService(
            CommentRepository commentRepository,
            TicketRepository ticketRepository,
            UserRepository userRepository
    ) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    public CommentResponse addComment(
            Long ticketId,
            CreateCommentRequest request,
            String userEmail
    ) {
        Ticket ticket = ticketRepository
                .findById(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ticket was not found."
                        )
                );

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Logged-in user was not found."
                        )
                );
boolean isTicketCreator =
        ticket.getCreatedBy()
                .getId()
                .equals(user.getId());

boolean isAssignedAgent =
        ticket.getAssignedTo() != null
        && ticket.getAssignedTo()
                .getId()
                .equals(user.getId());

boolean isAdmin =
        user.getRole()
                == com.supportdesk.user.Role.ADMIN;

if (!isTicketCreator
        && !isAssignedAgent
        && !isAdmin) {

    throw new ForbiddenOperationException(
            "You cannot comment on this ticket."
    );
}
        Comment comment = new Comment();

        comment.setTicket(ticket);
        comment.setUser(user);
        comment.setMessage(request.message().trim());
        comment.setCreatedAt(OffsetDateTime.now());

        Comment savedComment =
                commentRepository.save(comment);

        return CommentResponse.from(savedComment);
    }

    public List<CommentResponse> getComments(
            Long ticketId
    ) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException(
                    "Ticket was not found."
            );
        }

        return commentRepository
                .findByTicketIdOrderByCreatedAtAsc(ticketId)
                .stream()
                .map(CommentResponse::from)
                .toList();
    }
}