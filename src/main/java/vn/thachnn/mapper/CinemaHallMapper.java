package vn.thachnn.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.thachnn.dto.request.HallCreateRequest;
import vn.thachnn.dto.response.CinemaHallResponse;
import vn.thachnn.model.CinemaHall;

@Mapper(componentModel = "spring")
public interface CinemaHallMapper {

    CinemaHall toCinemaHall(HallCreateRequest request);

    @Mapping(source = "cinema.id", target = "cinema.id")
    @Mapping(source = "cinema.name", target = "cinema.name")
    CinemaHallResponse toCinemaHallResponse(CinemaHall hall);
}
