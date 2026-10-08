package com.supportdesk.comment;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(
            CommentService commentService
    ) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<CommentResponse> getComments(
            @PathVariable Long ticketId
    ) {
        return commentService.getComments(ticketId);
    }

    @PostMapping
    public CommentResponse addComment(
            @PathVariable Long ticketId,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication
    ) {
        return commentService.addComment(
                ticketId,
                request,
                authentication.getName()
        );
    }
}