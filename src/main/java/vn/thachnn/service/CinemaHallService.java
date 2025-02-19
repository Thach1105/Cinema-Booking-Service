package vn.thachnn.service;

import vn.thachnn.dto.request.HallCreateRequest;
import vn.thachnn.dto.request.HallUpdateRequest;
import vn.thachnn.dto.response.CinemaHallResponse;

import java.util.List;

public interface CinemaHallService {

    CinemaHallResponse create(HallCreateRequest request);

    CinemaHallResponse update(HallUpdateRequest request);

    void delete();

    CinemaHallResponse getCinemaHall(Long cinemaHallId);

    List<CinemaHallResponse> geyByCinema(Long cinemaId);
}
