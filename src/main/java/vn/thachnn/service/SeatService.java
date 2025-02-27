package vn.thachnn.service;

import vn.thachnn.dto.request.SeatUpdateRequest;
import vn.thachnn.dto.response.SeatResponse;
import vn.thachnn.model.Seat;

import java.util.List;

public interface SeatService {

    void changeType(SeatUpdateRequest request);

    List<SeatResponse> getSeatsOfHall(Long cinemaHallId);

    List<Seat> getSeatsById(List<Long> ids);

    Seat getById(Long id);
}
