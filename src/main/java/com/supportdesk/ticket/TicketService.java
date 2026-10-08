package com.supportdesk.ticket;

import com.supportdesk.category.Category;
import com.supportdesk.category.CategoryRepository;
import com.supportdesk.user.User;
import com.supportdesk.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

import com.supportdesk.exception.ForbiddenOperationException;
import com.supportdesk.exception.ResourceNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@Service
public class TicketService {

        private final TicketRepository ticketRepository;
        private final CategoryRepository categoryRepository;
        private final UserRepository userRepository;

        public TicketService(
                        TicketRepository ticketRepository,
                        CategoryRepository categoryRepository,
                        UserRepository userRepository) {
                this.ticketRepository = ticketRepository;
                this.categoryRepository = categoryRepository;
                this.userRepository = userRepository;
        }

        public List<TicketResponse> getMyTickets(
                        String userEmail) {
                return ticketRepository
                                .findByCreatedByEmail(userEmail)
                                .stream()
                                .map(TicketResponse::from)
                                .toList();
        }

        public TicketResponse createTicket(
                        CreateTicketRequest request,
                        String userEmail) {

                User createdBy = userRepository
                                .findByEmail(userEmail)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Logged-in user was not found."));

                Category category = categoryRepository
                                .findById(request.categoryId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Category was not found."));

                if (!category.isActive()) {
                        throw new ResourceNotFoundException(
                                        "This category is inactive.");
                }

                Ticket ticket = new Ticket();

                ticket.setTitle(request.title().trim());
                ticket.setDescription(request.description().trim());
                ticket.setCategory(category);

                ticket.setCreatedBy(createdBy);
                ticket.setAssignedTo(null);

                ticket.setPriority(request.priority());
                ticket.setStatus(TicketStatus.OPEN);

                OffsetDateTime now = OffsetDateTime.now();

                ticket.setCreatedAt(now);
                ticket.setUpdatedAt(now);

                Ticket savedTicket = ticketRepository.save(ticket);

                return TicketResponse.from(savedTicket);
        }

        public TicketResponse claimTicket(
                        Long ticketId,
                        String agentEmail) {
                User agent = userRepository
                                .findByEmail(agentEmail)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Support agent was not found."));

                Ticket ticket = ticketRepository
                                .findById(ticketId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Ticket was not found."));

                if (ticket.getAssignedTo() != null) {
                        throw new IllegalArgumentException(
                                        "This ticket is already assigned.");
                }

                if (ticket.getStatus() != TicketStatus.OPEN) {
                        throw new IllegalArgumentException(
                                        "Only open tickets can be claimed.");
                }

                ticket.setAssignedTo(agent);
                ticket.setStatus(TicketStatus.ASSIGNED);
                ticket.setUpdatedAt(OffsetDateTime.now());

                Ticket savedTicket = ticketRepository.save(ticket);

                return TicketResponse.from(savedTicket);
        }

        public List<TicketResponse> getMyAssignedTickets(
                        String agentEmail) {
                return ticketRepository
                                .findByAssignedToEmail(agentEmail)
                                .stream()
                                .map(TicketResponse::from)
                                .toList();
        }

        public TicketResponse updateStatus(
                        Long ticketId,
                        UpdateTicketStatusRequest request,
                        String agentEmail) {
                Ticket ticket = ticketRepository
                                .findById(ticketId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Ticket was not found."));

                User agent = userRepository
                                .findByEmail(agentEmail)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Logged-in user was not found."));

                if (ticket.getAssignedTo() == null) {
                        throw new IllegalArgumentException(
                                        "This ticket is not assigned.");
                }

                boolean isAssignedAgent = ticket.getAssignedTo() != null
                                && ticket.getAssignedTo()
                                                .getId()
                                                .equals(agent.getId());

                boolean isAdmin = agent.getRole() == com.supportdesk.user.Role.ADMIN;

                if (!isAssignedAgent && !isAdmin) {
                        throw new com.supportdesk.exception.ForbiddenOperationException(
                                        "You cannot change the status of this ticket.");
                }

                if (!isValidStatusTransition(
                                ticket.getStatus(),
                                request.status())) {
                        throw new IllegalArgumentException(
                                        "Invalid ticket status transition from "
                                                        + ticket.getStatus()
                                                        + " to "
                                                        + request.status()
                                                        + ".");
                }

                ticket.setStatus(request.status());
                ticket.setUpdatedAt(OffsetDateTime.now());

                Ticket savedTicket = ticketRepository.save(ticket);

                return TicketResponse.from(savedTicket);
        }

        private boolean isValidStatusTransition(
                        TicketStatus currentStatus,
                        TicketStatus newStatus) {
                return switch (currentStatus) {

                        case OPEN ->
                                newStatus == TicketStatus.ASSIGNED;

                        case ASSIGNED ->
                                newStatus == TicketStatus.IN_PROGRESS;

                        case IN_PROGRESS ->
                                newStatus == TicketStatus.WAITING_FOR_USER
                                                || newStatus == TicketStatus.RESOLVED;

                        case WAITING_FOR_USER ->
                                newStatus == TicketStatus.IN_PROGRESS
                                                || newStatus == TicketStatus.RESOLVED;

                        case RESOLVED ->
                                newStatus == TicketStatus.CLOSED
                                                || newStatus == TicketStatus.IN_PROGRESS;

                        case CLOSED -> false;
                };
        }

      

        public TicketResponse getTicketById(Long ticketId) {
                Ticket ticket = ticketRepository
                                .findById(ticketId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Ticket was not found."));

                return TicketResponse.from(ticket);
        }

        public TicketResponse updatePriority(
                        Long ticketId,
                        UpdateTicketPriorityRequest request,
                        String userEmail) {
                Ticket ticket = ticketRepository
                                .findById(ticketId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Ticket was not found."));

                User user = userRepository
                                .findByEmail(userEmail)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Logged-in user was not found."));

                boolean isAssignedAgent = ticket.getAssignedTo() != null
                                && ticket.getAssignedTo()
                                                .getId()
                                                .equals(user.getId());

                boolean isAdmin = user.getRole() == com.supportdesk.user.Role.ADMIN;

                if (!isAssignedAgent && !isAdmin) {
                        throw new ForbiddenOperationException(
                                        "You cannot change the priority of this ticket.");
                }

                ticket.setPriority(request.priority());
                ticket.setUpdatedAt(OffsetDateTime.now());

                Ticket savedTicket = ticketRepository.save(ticket);

                return TicketResponse.from(savedTicket);
        }

        public Page<TicketResponse> searchTickets(
                        String search,
                        Long categoryId,
                        TicketStatus status,
                        TicketPriority priority,
                        int page,
                        int size) {
                Specification<Ticket> specification = TicketSpecification.hasSearchText(search)
                                .and(TicketSpecification.hasCategory(categoryId))
                                .and(TicketSpecification.hasStatus(status))
                                .and(TicketSpecification.hasPriority(priority));

                Pageable pageable = PageRequest.of(
                                page,
                                size,
                                Sort.by("createdAt").descending());

                return ticketRepository
                                .findAll(specification, pageable)
                                .map(TicketResponse::from);
        }

        

}