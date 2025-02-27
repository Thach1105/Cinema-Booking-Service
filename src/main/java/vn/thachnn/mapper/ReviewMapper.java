package vn.thachnn.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.thachnn.dto.request.ReviewRequest;
import vn.thachnn.dto.response.ReviewResponse;
import vn.thachnn.model.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    Review toReview(ReviewRequest request);

    @Mapping(source = "user.fullName", target = "userFullName")
    @Mapping(source = "movie.title", target = "movieName")
    ReviewResponse toReviewResponse(Review review);
}
