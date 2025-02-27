package vn.thachnn.service;

import org.springframework.data.domain.Page;
import vn.thachnn.dto.request.ReviewRequest;
import vn.thachnn.dto.response.ReviewResponse;
import vn.thachnn.model.Review;
import vn.thachnn.model.User;

public interface ReviewService {
    ReviewResponse createReviewMovie(ReviewRequest request);

    Page<Review> getReviewsForMovie(Long movieId, Integer rating, int pageNumber, int pageSize);

    ReviewResponse getReviewResponseById(Long reviewId);

    Review getById(Long reviewId);

    ReviewResponse updateReview(Long reviewId, ReviewRequest request, Long userId);

    void delete(Long reviewId, User user);
}
