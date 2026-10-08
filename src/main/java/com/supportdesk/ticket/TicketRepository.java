package com.supportdesk.ticket;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;



public interface TicketRepository
        extends JpaRepository<Ticket, Long>,
                JpaSpecificationExecutor<Ticket> {

    List<Ticket> findByCreatedByEmail(String email);

    List<Ticket> findByAssignedToEmail(String email);

    List<Ticket> findByStatus(TicketStatus status);
}
