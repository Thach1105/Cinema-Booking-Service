package vn.thachnn.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.thachnn.dto.request.SeatUpdateRequest;
import vn.thachnn.dto.response.SeatResponse;
import vn.thachnn.exception.BadRequestException;
import vn.thachnn.mapper.SeatMapper;
import vn.thachnn.model.CinemaHall;
import vn.thachnn.model.Seat;
import vn.thachnn.repository.SeatRepository;
import vn.thachnn.service.SeatService;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "SEAT-SERVICE")
public class SeatServiceImpl implements SeatService {
    private final SeatMapper seatMapper;

    private final SeatRepository seatRepository;
    private final CinemaHallServiceImpl cinemaHallService;

    @Override
    @Transactional
    public void changeType(SeatUpdateRequest request) {
        log.info("Changing seat type of seat list");
        List<Seat> seats = getSeatsById(request.getIds());
        seats.forEach(seat -> seat.setType(request.getType()));

        seats = seatRepository.saveAll(seats);
        log.info("Changed seat type");
    }

    @Override
    public List<SeatResponse> getSeatsOfHall(Long cinemaHallId) {
        log.info("Get seats of cinema hall {}", cinemaHallId);
        CinemaHall cinemaHall = cinemaHallService.getById(cinemaHallId);
        /*List<Seat> seats = seatRepository.findAllByHall(cinemaHall);*/
        List<Seat> seats = cinemaHall.getSeats().stream()
                .sorted(Comparator.comparing(Seat::getId))
                .toList();

        return seats.stream().map(seatMapper::toSeatResponse).toList();
    }

    @Override
    public List<Seat> getSeatsById(List<Long> ids) {
        return seatRepository.findAllById(ids);
    }

    @Override
    public Seat getById(Long id) {
        return seatRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Seat not found"));
    }
}
