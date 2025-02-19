package vn.thachnn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.thachnn.dto.request.SeatUpdateRequest;
import vn.thachnn.dto.response.ApiResponse;
import vn.thachnn.service.Impl.SeatServiceImpl;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/seat")
@Tag(name = "Seat Controller")
@Slf4j(topic = "SEAT-CONTROLLER")
public class SeatController {

    private final SeatServiceImpl seatService;

    @PutMapping("/change-seat-type")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Change type of seat list", description = "API edit type of seat list to database")
    public ResponseEntity<?> changeStatusSeats(
            @RequestBody @Valid SeatUpdateRequest request
    ){
        log.info("Update seat list");
        seatService.changeType(request);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Changed Seat type successfully")
                .data("")
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/cinema-hall/{cinemaHallId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get seat list", description = "API get seat list of cinema hall from database")
    public ResponseEntity<?> getSeatByCinemaHall(
            @PathVariable Long cinemaHallId
    ){
        log.info("Get list seat of cinema hall {}", cinemaHallId);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("seats")
                .data(seatService.getSeatsOfHall(cinemaHallId))
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
