package vn.thachnn.service;

import org.springframework.data.domain.Page;
import vn.thachnn.dto.request.CinemaRequest;
import vn.thachnn.dto.response.CinemaResponse;
import vn.thachnn.model.Cinema;

public interface CinemaService {

    CinemaResponse create(CinemaRequest request);

    CinemaResponse update(Long cinemaId, CinemaRequest request);

    Cinema getById(Long cinemaId);

    Page<Cinema> getList(String city, Integer pageNumber, Integer pageSize);

    void delete(Long cinemaId);
}
