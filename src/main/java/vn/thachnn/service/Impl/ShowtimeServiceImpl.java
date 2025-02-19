package vn.thachnn.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.thachnn.dto.request.ShowtimeRequest;
import vn.thachnn.dto.response.ShowtimeDetailResponse;
import vn.thachnn.dto.response.ShowtimeResponse;
import vn.thachnn.dto.response.ShowtimeSeatResponse;
import vn.thachnn.exception.BadRequestException;
import vn.thachnn.exception.ResourceNotFoundException;
import vn.thachnn.mapper.ShowtimeMapper;
import vn.thachnn.model.*;
import vn.thachnn.repository.ShowtimeRepository;
import vn.thachnn.service.ShowtimeService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "SHOWTIME-SERVICE")
public class ShowtimeServiceImpl implements ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieServiceImpl movieService;
    private final CinemaHallServiceImpl cinemaHallService;
    private final ShowtimeSeatsServiceImpl showtimeSeatsService;
    private final ShowtimeMapper showtimeMapper;
    private final CinemaServiceImpl cinemaService;

    @Override
    @Transactional
    public ShowtimeResponse create(ShowtimeRequest request){
        log.info("Creating new showtime: {}", request);

        //find movie and cinema hall
        Movie movie = movieService.getMovie(request.getMovieId());
        CinemaHall hall = cinemaHallService.getById(request.getHallId());

        // check if the showtime overlaps with any existing showtime in the same cinema hall
        if(showtimeRepository.checkOverlappingShowtime(
                hall, request.getStartTime(), request.getEndTime())){
            throw new BadRequestException("The selected showtime overlaps with an existing showtime in the same hall");
        }

        // create new showtime
        Showtime showtime = Showtime.builder()
                .movie(movie)
                .hall(hall)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .price(request.getPrice())
                .build();

        showtime = showtimeRepository.save(showtime);

        //create seat for this showtime
        showtimeSeatsService.createSeatsForShowtime(showtime);

        return showtimeMapper.toShowtimeResponse(showtime);
    }

    @Override
    public ShowtimeResponse getShowtimeById(Long showtimeId){
        log.info("finding showtime by id");
        Showtime showtime = getById(showtimeId);
        return showtimeMapper.toShowtimeResponse(showtime);
    }

    @Override
    public List<ShowtimeDetailResponse> getShowtimeForUser(Long movieId, Long cinemaId, LocalDate selectedDate){
        log.info("Finding all showtime for user with movieId {} and cinemaId {}", movieId, cinemaId);

        Movie movie = movieId == null ? null : movieService.getMovie(movieId);
        Cinema cinema = cinemaService.getById(cinemaId);

        List<Showtime> showtimes = showtimeRepository.getAllShowtimeForUser(cinema, movie, selectedDate);
        return showtimes.stream().map(showtimeMapper::toShowtimeDetailResponse).toList();

    }

    // get list of seats for showtime by showtimeID
    @Override
    public List<ShowtimeSeatResponse> getSeatsForShowtime(Long showtimeId){

        //get showtime
        Showtime showtime = getById(showtimeId);
        log.info("Finding all seats of showtime: {}", showtimeId);

        //get list ShowtimeSeats
        List<ShowtimeSeats> seatsList = showtimeSeatsService.getSeatListOfShowtime(showtime);

        // convert ShowtimeSeats to ShowtimeSeatResponse DTO
        List<ShowtimeSeatResponse> seatResponseList = new ArrayList<>();
        for (ShowtimeSeats ss : seatsList){
            ShowtimeSeatResponse seatResponse = ShowtimeSeatResponse.builder()
                    .id(ss.getId())
                    .seatNumber(ss.getSeat().getSeatNumber())
                    .seatType(ss.getSeat().getType())
                    .status(ss.getStatus())
                    .build();

            seatResponseList.add(seatResponse);
        }

        return seatResponseList;
    }

    public Showtime getById(Long id){
        return showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));
    }
}
