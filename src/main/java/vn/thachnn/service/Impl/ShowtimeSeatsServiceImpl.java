package vn.thachnn.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.thachnn.common.SeatStatus;
import vn.thachnn.exception.ResourceNotFoundException;
import vn.thachnn.model.CinemaHall;
import vn.thachnn.model.Seat;
import vn.thachnn.model.Showtime;
import vn.thachnn.model.ShowtimeSeats;
import vn.thachnn.repository.ShowtimeSeatsRepository;
import vn.thachnn.service.ShowtimeSeatsService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "SHOWTIME-SEAT-SERVICE")
public class ShowtimeSeatsServiceImpl implements ShowtimeSeatsService {

    private final ShowtimeSeatsRepository showtimeSeatsRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private final int TTL_IN_SECONDS = 310;

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

        showtimeSeatsRepository.saveAll(seatsForShowtime);
    }

    @Override
    public List<ShowtimeSeats> getSeatListOfShowtime(Showtime showtime) {

        return showtimeSeatsRepository.findAllByShowtime(showtime);
    }

    @Override
    public int countAvailableSeatsForShowtime(Showtime showtime) {
        return showtimeSeatsRepository.countAvailableSeats(showtime);
    }

    @Override
    public ShowtimeSeats getById(Long showtimeSeatId) {
        return showtimeSeatsRepository.findById(showtimeSeatId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime Seat not found"));
    }

    @Override
    public List<ShowtimeSeats> getAllByIds(List<Long> ids) {
        return showtimeSeatsRepository.findAllById(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSeatStatusToBooked(List<Long> ids) {
        log.info("Updating seats {} to BOOKED", ids);
        List<ShowtimeSeats> seatsList = new ArrayList<>();

        for (Long id : ids){
            ShowtimeSeats seat = getById(id);

            if(seat.getStatus() != SeatStatus.BOOKED){
                seat.setStatus(SeatStatus.BOOKED);
                seatsList.add(seat);
            }
        }
        showtimeSeatsRepository.saveAll(seatsList);
    }


    //kiểm tra trạng thái ghế trên Redis và cơ sở dữ liệu
    /*@Cacheable(value = "status",
            key = "'showtime:'.concat(T(String).valueOf(#showtime.id)).concat(':seat:').concat(T(String).valueOf(#ss.id))")*/
    @Override
    public String isAvailable(Showtime showtime, ShowtimeSeats ss) {

        String key = "showtime:" + showtime.getId() + ":seatId:" + ss.getId();

        //check the seat status in redis
        Map<Object, Object> seatData = redisTemplate.opsForHash().entries(key);
        if(!seatData.isEmpty()){
            SeatStatus status = (SeatStatus) seatData.get("status");
            /*if(!status.equals(SeatStatus.AVAILABLE)) {
                return false;
            }*/
            return status.toString();
        }

        //check the seat status in db
        return ss.getStatus().toString();
        /*return ss.getStatus().equals(SeatStatus.AVAILABLE);*/
    }

    // tạm giữ trạng thái ghế trên Redis
    /*@CachePut(value = "status",
            key = "'showtime:'.concat(T(String).valueOf(#showtime.id)).concat(':seat:').concat(T(String).valueOf(#ss.id))")*/
    @Override
    public String reservedSeatInRedis(Showtime showtime, ShowtimeSeats ss, Map<String, Object> data) {

        String key = "showtime:" + showtime.getId() + ":seatId:" + ss.getId();

        redisTemplate.opsForHash().putAll(key,data);
        redisTemplate.expire(key, TTL_IN_SECONDS, TimeUnit.SECONDS);
        log.info("Hold seat {} successfully", ss.getSeat().getSeatNumber());

        return SeatStatus.PENDING.toString();
    }
}
