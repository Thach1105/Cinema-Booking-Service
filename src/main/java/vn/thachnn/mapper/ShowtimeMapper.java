package vn.thachnn.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.thachnn.dto.response.ShowtimeDetailResponse;
import vn.thachnn.model.Showtime;

@Mapper(componentModel = "spring")
public interface ShowtimeMapper {

    @Mapping(source = "movie.title", target = "movieName")
    @Mapping(source = "hall.name", target = "cinemaHallName")
    @Mapping(source = "hall.cinema.name", target = "cinemaName")
    @Mapping(source = "movie.ageLimit", target = "ageLimit")
    @Mapping(ignore = true, target = "seats")
    ShowtimeDetailResponse toShowtimeDetailResponse(Showtime showtime);
}
