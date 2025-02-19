package vn.thachnn.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.thachnn.common.SeatStatus;
import vn.thachnn.model.CinemaHall;
import vn.thachnn.model.Seat;
import vn.thachnn.model.Showtime;
import vn.thachnn.model.ShowtimeSeats;
import vn.thachnn.repository.ShowtimeSeatsRepository;
import vn.thachnn.service.ShowtimeSeatsService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "SHOWTIME-SEAT-SERVICE")
public class ShowtimeSeatsServiceImpl implements ShowtimeSeatsService {

    private final CinemaHallServiceImpl cinemaHallService;
    private final ShowtimeSeatsRepository showtimeSeatsRepository;

    @Override
    public void createSeatsForShowtime(Showtime showtime) {

        log.info("Create seats for showtime {}", showtime);
        CinemaHall hall = showtime.getHall();
        List<Seat> seats = hall.getSeats().stream()
                .sorted(Comparator.comparing(Seat::getId))
                .toList();

        List<ShowtimeSeats> seatsForShowtime = new ArrayList<>();

        for (Seat seat : seats){
            ShowtimeSeats showtimeSeat = ShowtimeSeats.builder()
                    .showtime(showtime)
                    .seat(seat)
                    .status(SeatStatus.AVAILABLE)
                    .build();

            seatsForShowtime.add(showtimeSeat);
        }

        seatsForShowtime = showtimeSeatsRepository.saveAll(seatsForShowtime);
    }

    @Override
    public List<ShowtimeSeats> getSeatListOfShowtime(Showtime showtime){

        return showtimeSeatsRepository.findAllByShowtime(showtime);
    }
}
