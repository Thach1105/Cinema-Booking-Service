package vn.thachnn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.thachnn.dto.request.ReviewRequest;
import vn.thachnn.dto.response.ApiResponse;
import vn.thachnn.dto.response.PaginationResponse;
import vn.thachnn.dto.response.ReviewResponse;
import vn.thachnn.mapper.ReviewMapper;
import vn.thachnn.model.Review;
import vn.thachnn.model.User;
import vn.thachnn.service.Impl.ReviewServiceImpl;

import java.util.List;

@Validated
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
@Tag(name = "Review Controller")
@Slf4j(topic = "REVIEW-CONTROLLER")
public class ReviewController {
    private final ReviewMapper reviewMapper;

    private final ReviewServiceImpl reviewService;

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Create a new review",
            description = "API allows a user to create a new review for a movie. The user must be authenticated.")
    public ResponseEntity<?> create(
            @RequestBody @Valid ReviewRequest request
    ){
        log.info("Create new review: {}", request);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Created review successfully")
                .data(reviewService.createReviewMovie(request))
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/for-movie/{movieId}")
    @Operation(summary = "Get reviews for a movie",
            description = "Retrieves all reviews for a specific movie. Optionally, reviews can be filtered by rating.")
    public ResponseEntity<?> getReviewForMovie(
        @PathVariable Long movieId,
        @RequestParam(required = false) Integer rating,
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        log.info("Get review for Movie {}", movieId);

        Page<Review> reviewPage = reviewService.getReviewsForMovie(movieId, rating, pageNumber, pageSize);

        List<Review> reviews = reviewPage.getContent();
        List<ReviewResponse> dataResponse = reviews.stream().map(reviewMapper::toReviewResponse).toList();

        PaginationResponse pagination = PaginationResponse.builder()
                .pageNumber(reviewPage.getNumber() + 1)
                .pageSize(reviewPage.getSize())
                .totalPages(reviewPage.getTotalPages())
                .totalElements(reviewPage.getTotalElements())
                .build();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.ACCEPTED.value())
                .message("reviews")
                .data(dataResponse)
                .pagination(pagination)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "Get a single review by ID",
            description = "Retrieve a specific review by its ID.")
    public ResponseEntity<?> getReview(
            @PathVariable Long reviewId
    ) {
        log.info("Get review with id: {}", reviewId);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("review")
                .data(reviewService.getReviewResponseById(reviewId))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "Update a review",
            description = "Allows a user to update a specific review by its ID. " +
                    "Only the user who created the review or an admin can update it.")
    public ResponseEntity<?> updateReview (
            @PathVariable Long reviewId,
            @RequestBody ReviewRequest request,
            @AuthenticationPrincipal User user
    ) {
        log.info("update review with id: {}", reviewId);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("review")
                .data(reviewService.updateReview(reviewId, request, user.getId()))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @Operation(summary = "Delete a review", description = """
            Allows a user or an admin to delete a review by its ID.
            The user can only delete their own review, while an admin can delete any review
            """)
    public ResponseEntity<?> deleteReview (
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User user
    ) {
        log.info("delete review with id: {}", reviewId);
        reviewService.delete(reviewId, user);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Deleted review successfully")
                .data("")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
