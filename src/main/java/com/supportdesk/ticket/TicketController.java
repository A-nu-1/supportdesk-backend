package com.supportdesk.ticket;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

        private final TicketService ticketService;

        public TicketController(
                        TicketService ticketService) {
                this.ticketService = ticketService;
        }

        @PostMapping
        public TicketResponse createTicket(
                        @Valid @RequestBody CreateTicketRequest request,
                        Authentication authentication) {
                return ticketService.createTicket(
                                request,
                                authentication.getName());
        }

        @GetMapping("/my")
        public List<TicketResponse> getMyTickets(
                        Authentication authentication) {
                return ticketService.getMyTickets(
                                authentication.getName());
        }

        @PostMapping("/{ticketId}/claim")
        public TicketResponse claimTicket(
                        @PathVariable Long ticketId,
                        Authentication authentication) {
                return ticketService.claimTicket(
                                ticketId,
                                authentication.getName());
        }

        @GetMapping("/assigned-to-me")
        public List<TicketResponse> getMyAssignedTickets(
                        Authentication authentication) {
                return ticketService.getMyAssignedTickets(
                                authentication.getName());
        }

        @PatchMapping("/{ticketId}/status")
        public TicketResponse updateStatus(
                        @PathVariable Long ticketId,
                        @Valid @RequestBody UpdateTicketStatusRequest request,
                        Authentication authentication) {
                return ticketService.updateStatus(
                                ticketId,
                                request,
                                authentication.getName());
        }

        @GetMapping
        public Page<TicketResponse> getAllTickets(
                        @RequestParam(required = false) String search,
                        @RequestParam(required = false) Long categoryId,
                        @RequestParam(required = false) TicketStatus status,
                        @RequestParam(required = false) TicketPriority priority,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                return ticketService.searchTickets(
                                search,
                                categoryId,
                                status,
                                priority,
                                page,
                                size);
        }

        @GetMapping("/{ticketId}")
        public TicketResponse getTicketById(
                        @PathVariable Long ticketId) {
                return ticketService.getTicketById(ticketId);
        }

        @PatchMapping("/{ticketId}/priority")
        public TicketResponse updatePriority(
                        @PathVariable Long ticketId,
                        @Valid @RequestBody UpdateTicketPriorityRequest request,
                        Authentication authentication) {
                return ticketService.updatePriority(
                                ticketId,
                                request,
                                authentication.getName());
        }
}