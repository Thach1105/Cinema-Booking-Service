package vn.thachnn.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.thachnn.dto.request.TicketRequest;
import vn.thachnn.dto.response.ApiResponse;
import vn.thachnn.dto.response.PaginationResponse;
import vn.thachnn.dto.response.TicketResponse;
import vn.thachnn.mapper.TicketMapper;
import vn.thachnn.model.Ticket;
import vn.thachnn.model.User;
import vn.thachnn.service.Impl.BookingService;
import vn.thachnn.service.Impl.TicketServiceImpl;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/ticket")
@Tag(name = "Ticket Controller")
@Slf4j(topic = "TICKET-CONTROLLER")
public class TicketController {

    private final BookingService bookingService;
    private final TicketServiceImpl ticketService;
    private final TicketMapper ticketMapper;

    @PostMapping("/booking")
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Book a Ticket",
            description = "This endpoint allows a user to request booking a ticket. " +
                    "The system will generate a session order for the user and temporarily hold the seat during the booking process.")
    public ResponseEntity<?> bookingTicket(
            @RequestBody TicketRequest request,
            @AuthenticationPrincipal User user
    ){
        log.info("User with id {} request to book a ticket", user.getId());
        String sessionOrder = bookingService.requestToBookTicket(request, user);

        JsonNode response = new ObjectMapper().createObjectNode()
                .put("sessionId", sessionOrder);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("session order")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/my-ticket")
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Get User's Tickets",
            description = "API fetches the list of tickets booked by the authenticated user.")
    public ResponseEntity<?> getMyUserTicket(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @AuthenticationPrincipal User user
    ){
        log.info("Get list ticket with user: {}", user);
        Page<Ticket> ticketPage = ticketService.getListTicketForUser(user, pageNumber, pageSize);

        List<Ticket> tickets = ticketPage.getContent();
        List<TicketResponse> responseList = tickets.stream()
                .map(ticketMapper::toTicketResponse).toList();

        PaginationResponse pagination = PaginationResponse.builder()
                .pageNumber(ticketPage.getNumber()+1)
                .pageSize(ticketPage.getSize())
                .totalPages(ticketPage.getTotalPages())
                .totalElements(ticketPage.getTotalElements())
                .build();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("tickets")
                .data(responseList)
                .pagination(pagination)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
