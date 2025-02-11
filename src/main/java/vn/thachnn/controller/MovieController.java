package vn.thachnn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.thachnn.dto.request.MovieRequest;
import vn.thachnn.dto.response.ApiResponse;
import vn.thachnn.service.Impl.MovieServiceImpl;

@RestController
@RequestMapping("/movie")
@Slf4j(topic = "MOVIE-CONTROLLER")
@Tag(name = "Movie Controller")
@RequiredArgsConstructor
public class MovieController {

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

    @Operation(summary = "Create movie", description = "API add new movie to database")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody MovieRequest request){
        log.info("Create new movie: {}", request);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Movie created successfully")
                .data(movieService.save(request))
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
    }

    @Operation(summary = "Update movie", description = "API update movie to database")
    @PutMapping("/{movieId}")
    public ResponseEntity<?> update(
            @PathVariable Long movieId,
            @RequestBody MovieRequest request
    ){
        log.info("Update movie by ID: {}", movieId);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.ACCEPTED.value())
                .message("Movie updated successfully")
                .data(movieService.update(request, movieId))
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.ACCEPTED);
    }
}
