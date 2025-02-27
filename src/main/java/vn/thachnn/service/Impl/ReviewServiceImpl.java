package vn.thachnn.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.thachnn.dto.request.ReviewRequest;
import vn.thachnn.dto.response.ReviewResponse;
import vn.thachnn.mapper.ReviewMapper;
import vn.thachnn.model.Movie;
import vn.thachnn.model.Review;
import vn.thachnn.model.User;
import vn.thachnn.repository.ReviewRepository;
import vn.thachnn.service.ReviewService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "REVIEW-SERVICE")
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieServiceImpl movieService;
    private final ReviewMapper reviewMapper;

    @Override
    public ReviewResponse createReviewMovie(ReviewRequest request) {
        log.info("Create review for movie {}", request.getMovieId());

        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Movie movie = movieService.getMovie(request.getMovieId());

        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .user(user)
                .movie(movie)
                .build();

        review = reviewRepository.save(review);
        return reviewMapper.toReviewResponse(review);
    }
}
