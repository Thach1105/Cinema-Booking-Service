package vn.thachnn.service;

import vn.thachnn.dto.request.ShowtimeRequest;
import vn.thachnn.dto.response.ShowtimeDetailResponse;
import vn.thachnn.dto.response.ShowtimeResponse;
import vn.thachnn.dto.response.ShowtimeSeatResponse;

import java.time.LocalDate;
import java.util.List;

public interface ShowtimeService {

    ShowtimeResponse getShowtimeById(Long showtimeId);

    ShowtimeResponse create(ShowtimeRequest request);

    List<ShowtimeDetailResponse> getShowtimeForUser(Long movieId, Long cinemaId, LocalDate selectedDate);

    List<ShowtimeSeatResponse> getSeatsForShowtime(Long showtimeId);
}
