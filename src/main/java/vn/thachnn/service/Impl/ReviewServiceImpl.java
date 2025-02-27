package vn.thachnn.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.thachnn.dto.request.ReviewRequest;
import vn.thachnn.dto.response.ReviewResponse;
import vn.thachnn.exception.ForbiddenException;
import vn.thachnn.exception.ResourceNotFoundException;
import vn.thachnn.mapper.ReviewMapper;
import vn.thachnn.model.Movie;
import vn.thachnn.model.Review;
import vn.thachnn.model.User;
import vn.thachnn.repository.ReviewRepository;
import vn.thachnn.service.ReviewService;

import java.util.Collection;
import java.util.Objects;

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

    @Override
    public Page<Review> getReviewsForMovie(Long movieId, Integer rating, int pageNumber, int pageSize) {
        log.info("Get all reviews for movie with id {}", movieId);
        Movie movie = movieService.getMovie(movieId);

        Pageable pageable = PageRequest.of(pageNumber -1, pageSize);
        return reviewRepository.findAllReview(movie, pageable, rating);
    }

    @Override
    public ReviewResponse getReviewResponseById(Long reviewId){
        Review review = getById(reviewId);

        return reviewMapper.toReviewResponse(review);
    }

    @Override
    public Review getById(Long reviewId){
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request, Long userId){
        log.info("Updating review with id {}", reviewId);
        Review review = getById(reviewId);

        if(!Objects.equals(review.getUser().getId(), userId)) {
            throw new ForbiddenException("You do not have the necessary permissions to perform this action.");
        }

        review.setRating(review.getRating());
        review.setComment(request.getComment());

        review = reviewRepository.save(review);
        return reviewMapper.toReviewResponse(review);
    }

    @Override
    @Transactional(rollbackFor =  Exception.class)
    public void delete(Long reviewId, User user) {
        log.info("deleting review with id {}", reviewId);

        Review review = getById(reviewId);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        boolean hasUserRole = authorities.stream().anyMatch(authority -> authority.getAuthority().equals("USER"));
        if(hasUserRole && !Objects.equals(user.getId(), review.getUser().getId())) {
            throw new ForbiddenException("You do not have the necessary permissions to perform this action");
        }

        reviewRepository.deleteById(reviewId);
    }
}
