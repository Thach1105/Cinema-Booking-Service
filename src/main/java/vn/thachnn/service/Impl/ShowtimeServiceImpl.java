package vn.thachnn.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.thachnn.common.MovieStatus;
import vn.thachnn.common.SeatStatus;
import vn.thachnn.common.ShowtimeType;
import vn.thachnn.dto.request.ShowtimeRequest;
import vn.thachnn.dto.response.ShowtimeDetailResponse;
import vn.thachnn.dto.response.ShowtimeSeatResponse;
import vn.thachnn.exception.BadRequestException;
import vn.thachnn.exception.ResourceNotFoundException;
import vn.thachnn.mapper.ShowtimeMapper;
import vn.thachnn.model.*;
import vn.thachnn.repository.ShowtimeRepository;
import vn.thachnn.service.ShowtimeService;
import vn.thachnn.util.DateTimeUtils;

import java.time.Duration;
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
    private final TicketPriceServiceImpl ticketPriceService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShowtimeDetailResponse create(ShowtimeRequest request){
        log.info("Creating new showtime: {}", request);

        //find movie and cinema hall
        CinemaHall hall = cinemaHallService.getById(request.getHallId());
        Movie movie = movieService.getMovie(request.getMovieId());
        if(movie.getStatus() == MovieStatus.NONE){
            throw new BadRequestException("Movies with a status of NONE cannot create new showtime.");
        }

        // check if the showtime overlaps with any existing showtime in the same cinema hall
        long minutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
        log.info("showtime duration: {}", minutes);
        if(minutes <= movie.getDuration()){
            throw new BadRequestException("The showtime duration must be longer than the movie duration.");
        }
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
                .build();

        showtime = showtimeRepository.save(showtime);

        //create seat for this showtime
        showtimeSeatsService.createSeatsForShowtime(showtime);

        return showtimeMapper.toShowtimeDetailResponse(showtime);
    }

    @Override
    public ShowtimeDetailResponse getShowtimeById(Long showtimeId){
        log.info("finding showtime by id");
        Showtime showtime = getById(showtimeId);
        return showtimeMapper.toShowtimeDetailResponse(showtime);
    }

    @Override
    public List<ShowtimeDetailResponse> getListShowtime(Long movieId, Long cinemaId, LocalDate selectedDate){
        log.info("Finding all showtime for user with movieId {} and cinemaId {}", movieId, cinemaId);

        Movie movie = movieId == null ? null : movieService.getMovie(movieId);
        Cinema cinema = cinemaService.getById(cinemaId);

        List<Showtime> showtimes = showtimeRepository.getListShowtime(cinema, movie, selectedDate);
        List<ShowtimeDetailResponse> response = new ArrayList<>();
        for (Showtime s : showtimes){
            ShowtimeDetailResponse showtimeDetail = showtimeMapper.toShowtimeDetailResponse(s);
            showtimeDetail.setAvailableSeats(showtimeSeatsService.countAvailableSeatsForShowtime(s));
            response.add(showtimeDetail);
        }
        return response;
    }

    // get list of seats for showtime by showtimeID
    @Override
    public ShowtimeDetailResponse getSeatsForShowtime(Long showtimeId){

        log.info("Finding all seats of showtime: {}", showtimeId);
        //get showtime
        Showtime showtime = getById(showtimeId);
        ShowtimeDetailResponse detailResponse = showtimeMapper.toShowtimeDetailResponse(showtime);

        //get list ShowtimeSeats
        List<ShowtimeSeats> seatsList = showtimeSeatsService.getSeatListOfShowtime(showtime);

        // convert ShowtimeSeats to ShowtimeSeatResponse DTO
        List<ShowtimeSeatResponse> seatResponseList = new ArrayList<>();
        for (ShowtimeSeats ss : seatsList){

            //get price of seat in TicketPrice
            Integer price = ticketPriceService.getPriceByAttributes(
                    ShowtimeType.REGULAR,
                    showtime.getHall().getType(),
                    ss.getSeat().getType(),
                    DateTimeUtils.checkDayType(showtime.getStartTime()));

            //convert
            SeatStatus status = SeatStatus.valueOf(showtimeSeatsService.isAvailable(showtime, ss)) ;
            ShowtimeSeatResponse seatResponse = ShowtimeSeatResponse.builder()
                    .id(ss.getId())
                    .seatNumber(ss.getSeat().getSeatNumber())
                    .seatType(ss.getSeat().getType())
                    .status(status)
                    .price(price)
                    .build();

            seatResponseList.add(seatResponse);
        }
        detailResponse.setSeats(seatResponseList);

        return detailResponse;
    }

    public Showtime getById(Long id){
        return showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));
    }
}
