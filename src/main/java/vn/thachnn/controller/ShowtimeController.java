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
import vn.thachnn.dto.request.ShowtimeRequest;
import vn.thachnn.dto.response.ApiResponse;
import vn.thachnn.service.Impl.ShowtimeServiceImpl;

import java.time.LocalDate;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/showtime")
@Tag(name = "Showtime Controller")
@Slf4j(topic = "SHOWTIME-CONTROLLER")
public class ShowtimeController {

    private final ShowtimeServiceImpl showtimeService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create new showtime", description = "Api add new showtime to database")
    public ResponseEntity<?> createShowTime(@RequestBody @Valid ShowtimeRequest request){
        log.info("create new showtime");

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Created new showtime successfully")
                .data(showtimeService.create(request))
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{showtimeId}")
    @Operation(summary = "Get showtime by id", description = "Api get showtime by id from database")
    public ResponseEntity<?> getShowtimeById(
            @PathVariable Long showtimeId
    ){
        log.info("Get showtime by Id {}", showtimeId);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("showtime")
                .data(showtimeService.getShowtimeById(showtimeId))
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    /*PUBLIC*/
    @GetMapping("/{showtimeId}/list-of-seats")
    @Operation(summary = "Get list of seats for showtime",
            description = "This API retrieves the list of seats available for a specific showtime.")
    public ResponseEntity<?> getListSeatForShowtime(
            @PathVariable Long showtimeId
    ){
        log.info("Get all seats of showtime {}", showtimeId);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("seat list")
                .data(showtimeService.getSeatsForShowtime(showtimeId))
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    /*PUBLIC*/
    @GetMapping("/list-for-user")
    @Operation(summary = "Get list showtime for user", description = "Api get list showtime for user by movie and cinema")
    public ResponseEntity<?> getShowtimeForUser(
            @RequestParam(name = "cinemaId") Long cinemaId,
            @RequestParam(required = false, name = "movieId") Long movieId,
            @RequestParam(required = false) LocalDate selectedDate
    ){
        log.info("Find all showtime for user");

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("showtime list")
                .data(showtimeService.getShowtimeForUser(movieId, cinemaId, selectedDate))
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
