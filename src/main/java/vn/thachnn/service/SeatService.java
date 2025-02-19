package vn.thachnn.service;

import vn.thachnn.dto.request.SeatUpdateRequest;
import vn.thachnn.dto.response.SeatResponse;

import java.util.List;

public interface SeatService {

    void changeType(SeatUpdateRequest request);

    List<SeatResponse> getSeatsOfHall(Long cinemaHallId);
}
