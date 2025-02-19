package vn.thachnn.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.thachnn.dto.response.SeatResponse;
import vn.thachnn.model.Seat;

@Mapper(componentModel = "spring")
public interface SeatMapper {

    @Mapping(source = "hall.id", target = "cinemaHallId")
    SeatResponse toSeatResponse(Seat seat);
}
