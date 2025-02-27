package vn.thachnn.service;

import vn.thachnn.model.Showtime;
import vn.thachnn.model.ShowtimeSeats;

import java.util.List;
import java.util.Map;


public interface ShowtimeSeatsService {

    void createSeatsForShowtime(Showtime showtime);

    List<ShowtimeSeats> getSeatListOfShowtime(Showtime showtime);

    int countAvailableSeatsForShowtime(Showtime showtime);

    ShowtimeSeats getById(Long showtimeSeatId);

    List<ShowtimeSeats> getAllByIds(List<Long> ids);

    void updateSeatStatusToBooked(List<Long> ids);

    String isAvailable(Showtime showtime, ShowtimeSeats ss);

    String reservedSeatInRedis(Showtime showtime, ShowtimeSeats ss, Map<String, Object> data);
}
