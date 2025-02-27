package vn.thachnn.service;

import vn.thachnn.dto.request.ReviewRequest;
import vn.thachnn.dto.response.ReviewResponse;

public interface ReviewService {
    ReviewResponse createReviewMovie(ReviewRequest request);
}
