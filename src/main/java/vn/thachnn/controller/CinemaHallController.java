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
import vn.thachnn.dto.request.HallCreateRequest;
import vn.thachnn.dto.request.HallUpdateRequest;
import vn.thachnn.dto.response.ApiResponse;
import vn.thachnn.service.Impl.CinemaHallServiceImpl;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/cinema-hall")
@Tag(name = "CinemaHall Controller")
@Slf4j(topic = "CINEMA-HALL-CONTROLLER")
public class CinemaHallController {

    private final CinemaHallServiceImpl cinemaHallService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create cinema hall", description = "API add cinema hall to database")
    public ResponseEntity<?> create(@RequestBody HallCreateRequest request){
        log.info("Create new hall for cinema {}", request.getCinemaId());

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Created new hall successfully")
                .data(cinemaHallService.create(request))
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{cinemaHallId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get cinema hall", description = "API retrieve cinema hall from database")
    public ResponseEntity<?> getById(@PathVariable Long cinemaHallId){
        log.info("Get cinema hall by id: {}", cinemaHallId);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("cinema hall")
                .data(cinemaHallService.getCinemaHall(cinemaHallId))
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update cinema hall", description = "API update cinema hall to database")
    public ResponseEntity<?> update(@RequestBody @Valid HallUpdateRequest request){
        log.info("Update cinema hall with id: {}", request.getId());

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Updated Cinema hall successfully")
                .data(cinemaHallService.update(request))
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/cinema/{cinemaId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get list hall of one cinema", description = "API retrieve list cinema hall from database")
    public ResponseEntity<?> getCinemaHallsByCinemaId( @PathVariable Long cinemaId ){
        log.info("Get list cinema hall of cinema {}", cinemaId);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Cinema halls list")
                .data(cinemaHallService.geyByCinema(cinemaId))
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
