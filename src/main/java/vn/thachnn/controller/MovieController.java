package vn.thachnn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.thachnn.common.MovieStatus;
import vn.thachnn.dto.request.MovieRequest;
import vn.thachnn.dto.response.ApiResponse;
import vn.thachnn.dto.response.MovieResponse;
import vn.thachnn.dto.response.PaginationResponse;
import vn.thachnn.mapper.MovieMapper;
import vn.thachnn.model.Movie;
import vn.thachnn.service.Impl.MovieServiceImpl;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/movie")
@Slf4j(topic = "MOVIE-CONTROLLER")
@Tag(name = "Movie Controller")
@RequiredArgsConstructor
public class MovieController {

    private final MovieMapper movieMapper;
    private final MovieServiceImpl movieService;

    @Operation(summary = "Get movie detail", description = "API retrieve movie detail by ID from database")
    @GetMapping("/{movieId}")
    public ResponseEntity<?> getMovieDetail(@PathVariable Long movieId){
        log.info("Get movie detail by ID: {}", movieId);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("movie")
                .data(movieService.findById(movieId))
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create movie", description = "API add new movie to database")
    @PostMapping
    public ResponseEntity<?> create(
            @RequestPart(name = "movie") MovieRequest movie,
            @RequestPart(name = "banner") MultipartFile banner
    ){
        log.info("Create new movie: {}", movie);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Created movie successfully")
                .data(movieService.save(movie, banner))
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update movie", description = "API update movie to database")
    @PutMapping("/{movieId}")
    public ResponseEntity<?> update(
            @PathVariable Long movieId,
            @RequestBody MovieRequest request
    ){
        log.info("Update movie by ID: {}", movieId);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.ACCEPTED.value())
                .message("Update movie information successfully")
                .data(movieService.update(request, movieId))
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update movie banner", description = "API update url banner to database and upload new banner to cloud")
    @PatchMapping("/{movieId}/change-banner")
    public ResponseEntity<?> updateBanner(
            @PathVariable Long movieId,
            @RequestPart MultipartFile banner
    ){
        log.info("Update banner for movie id: {}", movieId);
        movieService.updateMovieBanner(movieId, banner);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Updated movie banner successfully")
                .data("")
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update movie status", description = "API update movie status to database")
    @PatchMapping("/{movieId}/change-status")
    public ResponseEntity<?> updateBanner(
            @PathVariable Long movieId,
            @RequestParam MovieStatus status
    ){
        log.info("Update status for movie id: {}", movieId);
        movieService.updateMovieStatus(movieId, status);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Updated movie status successfully")
                .data("")
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/public/list")
    @Operation(summary = "Get list movie for customer",
            description = "API to retrieve a list of currently showing or upcoming movies.")
    public ResponseEntity<?> getListMovie(
            @RequestParam MovieStatus status,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "8") int pageSize
    ){
        log.info("Find list movies {}", status.toString().toLowerCase());

        Page<Movie> moviePage = movieService.getListMoviePublic(pageNumber, pageSize, status);
        List<MovieResponse> responseList = moviePage.getContent()
                .stream().map(movieMapper::toMovieResponse).toList();

        PaginationResponse pagination = PaginationResponse.builder()
                .pageNumber(moviePage.getNumber()+1)
                .pageSize(moviePage.getSize())
                .totalPages(moviePage.getTotalPages())
                .totalElements(moviePage.getTotalElements())
                .build();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("list movie")
                .data(responseList)
                .pagination(pagination)
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/list")
    @Operation(summary = "", description = "")
    public ResponseEntity<?> getAll (
        @RequestParam(name = "title", required = false) String title,
        @RequestParam(name = "releaseDate", required = false) LocalDate releaseDate,
        @RequestParam(name = "minDuration", required = false) Integer minDuration,
        @RequestParam(name = "maxDuration", required = false) Integer maxDuration,
        @RequestParam(name = "ageLimitCondition", required = false) String ageLimitCondition,
        @RequestParam(name = "status", required = false) MovieStatus status,
        @RequestParam(name = "sortBy", required = false) String sortBy,
        @RequestParam(name = "direction", required = false) String direction,
        @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber,
        @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ){
        log.info("Find all movie");

        Page<Movie> moviePage = movieService.findAll(title,releaseDate, minDuration, maxDuration,
                ageLimitCondition, status, sortBy, direction, pageNumber, pageSize);

        List<MovieResponse> responseList = moviePage.getContent().stream()
                .map(movieMapper::toMovieResponse).toList();

        PaginationResponse pagination = PaginationResponse.builder()
                .pageNumber(moviePage.getNumber()+1)
                .pageSize(moviePage.getSize())
                .totalPages(moviePage.getTotalPages())
                .totalElements(moviePage.getTotalElements())
                .build();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("list movie")
                .data(responseList)
                .pagination(pagination)
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
