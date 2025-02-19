package vn.thachnn.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.thachnn.dto.response.ShowtimeDetailResponse;
import vn.thachnn.dto.response.ShowtimeResponse;
import vn.thachnn.model.Showtime;

@Mapper(componentModel = "spring")
public interface ShowtimeMapper {

    @Mapping(source = "movie.id", target = "movieId")
    @Mapping(source = "movie.title", target = "movieName")
    @Mapping(source = "hall.id", target = "hallId")
    ShowtimeResponse toShowtimeResponse(Showtime showtime);

    @Mapping(source = "movie.title", target = "movieName")
    @Mapping(source = "hall.name", target = "cinemaHallName")
    @Mapping(source = "hall.cinema.name", target = "cinemaName")
    ShowtimeDetailResponse toShowtimeDetailResponse(Showtime showtime);
}
