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
import vn.thachnn.dto.request.CinemaRequest;
import vn.thachnn.dto.response.ApiResponse;
import vn.thachnn.dto.response.CinemaResponse;
import vn.thachnn.dto.response.PaginationResponse;
import vn.thachnn.mapper.CinemaMapper;
import vn.thachnn.model.Cinema;
import vn.thachnn.service.Impl.CinemaServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
@RequestMapping("/cinema")
@RequiredArgsConstructor
@Slf4j(topic = "CINEMA-CONTROLLER")
@Tag(name = "Cinema Controller")
public class CinemaController {

    private final CinemaServiceImpl cinemaService;
    private final CinemaMapper cinemaMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create new cinema", description = "API add new cinema to database")
    public ResponseEntity<?> create(@RequestBody @Valid CinemaRequest request){
        log.info("Create new cinema: {}", request);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Created cinema successfully")
                .data(cinemaService.create(request))
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/{cinemaId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update cinema", description = "API update cinema to database")
    public ResponseEntity<?> update(@PathVariable @Min(1) Long cinemaId, @RequestBody @Valid CinemaRequest request){
        log.info("Update cinema with id: {}", cinemaId);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Updated cinema successfully")
                .data(cinemaService.update(cinemaId, request))
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{cinemaId}")
   /* @PreAuthorize("hasAuthority('ADMIN')")*/
    @Operation(summary = "Get cinema by ID", description = "API retrieve cinema by ID from database")
    public ResponseEntity<?> getCinema(@PathVariable @Min(1) Long cinemaId){
        log.info("Find cinema with id: {}", cinemaId);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("cinema")
                .data(cinemaService.getCinema(cinemaId))
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{cinemaId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete cinema", description = "API delete cinema")
    public ResponseEntity<?> delete(@PathVariable @Min(1) Long cinemaId){
        log.info("Delete cinema with id: {}", cinemaId);

        cinemaService.delete(cinemaId);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("deleted cinema successfully")
                .data("")
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get list cinema", description = "API retrieve list cinema by ID from database")
    public ResponseEntity<?> findAll(
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "pageNumber must be greater than or equal to 1" ) int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize
    ){
        log.info("Get cinema list");
        Page<Cinema> cinemaPage = cinemaService.getList(city, pageNumber, pageSize);
        List<CinemaResponse> cinemas = cinemaPage.getContent().stream()
                .map(cinemaMapper::toCinemaResponse).toList();

        PaginationResponse pagination = PaginationResponse.builder()
                .pageNumber(cinemaPage.getNumber())
                .pageSize(cinemaPage.getSize())
                .totalPages(cinemaPage.getTotalPages())
                .totalElements(cinemaPage.getTotalElements())
                .build();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("cinemas list")
                .data(cinemas)
                .pagination(pagination)
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
