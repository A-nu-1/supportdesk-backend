package com.supportdesk.ticket;

import com.supportdesk.category.CategoryRepository;
import com.supportdesk.user.User;
import com.supportdesk.user.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import com.supportdesk.category.Category;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

        @Mock
        private TicketRepository ticketRepository;

        @Mock
        private CategoryRepository categoryRepository;

        @Mock
        private UserRepository userRepository;

        @InjectMocks
        private TicketService ticketService;

        @Test
        void shouldNotAllowClaimingAlreadyAssignedTicket() {

                User agent = new User();
                agent.setId(2L);
                agent.setEmail("agent@example.com");

                User existingAgent = new User();
                existingAgent.setId(99L);

                Ticket ticket = new Ticket();
                ticket.setId(1L);
                ticket.setAssignedTo(existingAgent);
                ticket.setStatus(TicketStatus.ASSIGNED);

                when(userRepository.findByEmail("agent@example.com"))
                                .thenReturn(Optional.of(agent));

                when(ticketRepository.findById(1L))
                                .thenReturn(Optional.of(ticket));

                assertThrows(
                                IllegalArgumentException.class,
                                () -> ticketService.claimTicket(
                                                1L,
                                                "agent@example.com"));
        }

        @Test
        void shouldRejectInvalidStatusTransition() {

                User agent = new User();
                agent.setId(2L);
                agent.setEmail("agent@example.com");

                Ticket ticket = new Ticket();
                ticket.setId(1L);
                ticket.setAssignedTo(agent);
                ticket.setStatus(TicketStatus.WAITING_FOR_USER);

                when(ticketRepository.findById(1L))
                                .thenReturn(Optional.of(ticket));

                when(userRepository.findByEmail("agent@example.com"))
                                .thenReturn(Optional.of(agent));

                UpdateTicketStatusRequest request = new UpdateTicketStatusRequest(
                                TicketStatus.CLOSED);

                assertThrows(
                                IllegalArgumentException.class,
                                () -> ticketService.updateStatus(
                                                1L,
                                                request,
                                                "agent@example.com"));
        }

        @Test
        void shouldClaimOpenTicketSuccessfully() {

                User agent = new User();
                agent.setId(2L);
                agent.setEmail("agent@example.com");
                agent.setFirstName("Sam");
                agent.setLastName("Support");

                Category category = new Category();
                category.setId(1L);
                category.setName("Laptop");

                User creator = new User();
                creator.setId(1L);
                creator.setFirstName("Anu");
                creator.setLastName("Rajendra");

                Ticket ticket = new Ticket();
                ticket.setId(1L);
                ticket.setTitle("Laptop will not start");
                ticket.setDescription("My laptop does not power on.");
                ticket.setCategory(category);
                ticket.setCreatedBy(creator);
                ticket.setAssignedTo(null);
                ticket.setPriority(TicketPriority.HIGH);
                ticket.setStatus(TicketStatus.OPEN);

                when(userRepository.findByEmail("agent@example.com"))
                                .thenReturn(Optional.of(agent));

                when(ticketRepository.findById(1L))
                                .thenReturn(Optional.of(ticket));

                when(ticketRepository.save(ticket))
                                .thenReturn(ticket);

                ticketService.claimTicket(
                                1L,
                                "agent@example.com");

                assertEquals(agent, ticket.getAssignedTo());
                assertEquals(
                                TicketStatus.ASSIGNED,
                                ticket.getStatus());

                verify(ticketRepository).save(ticket);
        }
}