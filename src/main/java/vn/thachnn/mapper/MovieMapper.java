package vn.thachnn.mapper;

import org.mapstruct.Mapper;
import vn.thachnn.dto.request.MovieRequest;
import vn.thachnn.dto.response.MovieResponse;
import vn.thachnn.model.Movie;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    Movie toMovie(MovieRequest request);

    MovieResponse toMovieResponse(Movie movie);
}
