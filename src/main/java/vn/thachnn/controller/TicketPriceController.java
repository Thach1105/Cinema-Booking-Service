package vn.thachnn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.thachnn.common.DayType;
import vn.thachnn.common.RoomType;
import vn.thachnn.common.SeatType;
import vn.thachnn.common.ShowtimeType;
import vn.thachnn.dto.request.TicketPriceRequest;
import vn.thachnn.dto.response.ApiResponse;
import vn.thachnn.dto.response.PaginationResponse;
import vn.thachnn.dto.response.TicketPriceResponse;
import vn.thachnn.mapper.TicketPriceMapper;
import vn.thachnn.model.TicketPrice;
import vn.thachnn.service.Impl.TicketPriceServiceImpl;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/ticket-price")
@Tag(name = "TicketPrice Controller")
@Slf4j(topic = "TICKET-PRICE-CONTROLLER")
public class TicketPriceController {

    private final TicketPriceMapper ticketPriceMapper;
    private final TicketPriceServiceImpl ticketPriceService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create new tick price",
            description = "This API allows admins to create a new ticket price " +
                    "based on seat type, room type, showtime type, and day type.")
    public ResponseEntity<?> create(
            @RequestBody @Valid TicketPriceRequest request
    ){
        log.info("Create new TicketPrice: {}", request);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Created new TicketPrice successfully")
                .data(ticketPriceService.create(request))
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PatchMapping("/{ticketPriceId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update tick price",
            description = "This API allows admins to update a ticket price by Id")
    public ResponseEntity<?> update(
            @RequestParam @Min(value = 0, message = "price must be greater than or equal to 0") Integer price,
            @PathVariable Long ticketPriceId
    ){
        log.info("Update TicketPrice with id: {}", ticketPriceId);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.ACCEPTED.value())
                .message("Updated TicketPrice successfully")
                .data(ticketPriceService.update(ticketPriceId, price))
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{ticketPriceId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete tick price",
            description = "This API allows admins to delete a ticket price by Id")
    public ResponseEntity<?> deleteById(
            @PathVariable Long ticketPriceId
    ){
        log.info("Delete TicketPrice with id: {}", ticketPriceId);
        ticketPriceService.delete(ticketPriceId);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Deleted TicketPrice successfully")
                .data("")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{ticketPriceId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get ticket price by ID", description = "Retrieve details of a specific ticket price by its ID.")
    public ResponseEntity<?> getById(
            @PathVariable Long ticketPriceId
    ){
        log.info("Get TicketPrice with id: {}", ticketPriceId);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("TicketPrice")
                .data(ticketPriceService.getTicketPrice(ticketPriceId))
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
            summary = "Get list of ticket prices",
            description = "This API retrieves a paginated list of ticket prices based on optional filters " +
                    "such as showtime type, day type, room type, and seat type. "
    )
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) ShowtimeType showtimeType,
            @RequestParam(required = false) DayType dayType,
            @RequestParam(required = false) RoomType roomType,
            @RequestParam(required = false) SeatType seatType,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ){
        log.info("Get list TicketPrice");
        Page<TicketPrice> page = ticketPriceService.getAll(showtimeType, dayType, roomType,
                seatType, pageNumber, pageSize);

        List<TicketPriceResponse> responseList = page.getContent().stream()
                .map(ticketPriceMapper::toTicketPriceResponse).toList();

        PaginationResponse pagination = PaginationResponse.builder()
                .pageNumber(page.getNumber()+1)
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("List TicketPrice")
                .data(responseList)
                .pagination(pagination)
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
