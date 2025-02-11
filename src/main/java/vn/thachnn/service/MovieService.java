package vn.thachnn.service;

import vn.thachnn.dto.request.MovieRequest;
import vn.thachnn.dto.response.MovieResponse;

public interface MovieService {

    MovieResponse save(MovieRequest request);

    MovieResponse findById(Long id);

    MovieResponse update(MovieRequest request, Long movieId);

}
