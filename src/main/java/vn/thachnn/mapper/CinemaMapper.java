package vn.thachnn.mapper;

import org.mapstruct.Mapper;
import vn.thachnn.dto.request.CinemaRequest;
import vn.thachnn.dto.response.CinemaResponse;
import vn.thachnn.model.Cinema;

@Mapper(componentModel = "spring")
public interface CinemaMapper {

    Cinema toCinema(CinemaRequest request);

    CinemaResponse toCinemaResponse(Cinema cinema);
}
