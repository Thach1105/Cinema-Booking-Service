package vn.thachnn.service;

import vn.thachnn.model.Showtime;
import vn.thachnn.model.ShowtimeSeats;

import java.util.List;


public interface ShowtimeSeatsService {

    void createSeatsForShowtime(Showtime showtime);

    List<ShowtimeSeats> getSeatListOfShowtime(Showtime showtime);
}
