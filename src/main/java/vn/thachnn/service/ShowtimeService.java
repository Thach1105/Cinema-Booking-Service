package vn.thachnn.service;

import vn.thachnn.dto.request.ShowtimeRequest;
import vn.thachnn.dto.response.ShowtimeDetailResponse;

import java.time.LocalDate;
import java.util.List;

public interface ShowtimeService {

    ShowtimeDetailResponse getShowtimeById(Long showtimeId);

    ShowtimeDetailResponse create(ShowtimeRequest request);

    List<ShowtimeDetailResponse> getListShowtime(Long movieId, Long cinemaId, LocalDate selectedDate);

    ShowtimeDetailResponse getSeatsForShowtime(Long showtimeId);
}
