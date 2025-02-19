package vn.thachnn.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.thachnn.common.SeatType;
import vn.thachnn.dto.request.HallCreateRequest;
import vn.thachnn.dto.request.HallUpdateRequest;
import vn.thachnn.dto.response.CinemaHallResponse;
import vn.thachnn.exception.BadRequestException;
import vn.thachnn.exception.ResourceNotFoundException;
import vn.thachnn.mapper.CinemaHallMapper;
import vn.thachnn.repository.SeatRepository;
import vn.thachnn.model.Cinema;
import vn.thachnn.model.CinemaHall;
import vn.thachnn.model.Seat;
import vn.thachnn.repository.CinemaHallRepository;
import vn.thachnn.service.CinemaHallService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CINEMA-HALL-SERVICE")
public class CinemaHallServiceImpl implements CinemaHallService {

    private final SeatRepository seatRepository;
    private final CinemaHallMapper cinemaHallMapper;
    private final CinemaServiceImpl cinemaService;
    private final CinemaHallRepository cinemaHallRepository;

    @Override
    @Transactional
    public CinemaHallResponse create(HallCreateRequest request) {
        log.info("Creating new cinema hall for cinema {}", request.getCinemaId());

        //create new hall
        CinemaHall hall = cinemaHallMapper.toCinemaHall(request);
        Cinema cinema = cinemaService.getById(request.getCinemaId());
        if(cinemaHallRepository.existsByNameAndCinema(hall.getName(), cinema)){
            throw new BadRequestException("Duplicate name");
        }
        hall.setCinema(cinema);
        CinemaHall newHall = cinemaHallRepository.save(hall);
        log.info("Create new hall {}", newHall);

        //create seat for new hall
        List<Seat> seatList = new ArrayList<>(List.of());
        for(char r = 'A'; r <= 'A' + hall.getRow(); r++){
            for(int c = 1; c <= hall.getColumn(); c++){
                Seat seat = Seat.builder()
                        .hall(newHall)
                        .seatNumber(String.format("%c%d", r, c))
                        .type(SeatType.STANDARD)
                        .build();

                seatList.add(seat);
            }
        }
        seatRepository.saveAll(seatList);
        log.info("Create {} seats for new hall {}", hall.getTotalSeats(), hall.getId());

        return cinemaHallMapper.toCinemaHallResponse(newHall);
    }

    @Override
    @Transactional
    public CinemaHallResponse update(HallUpdateRequest request) {
        //Hall phải ngừng hoạt động thì mới có thể cập nhật thông tin
        CinemaHall hall = getById(request.getId());
        if(hall.isAvailable()){
            throw new BadRequestException("Cinema hall is active, cannot be edit");
        }

        // Update hall
        log.info("Updating cinema hall with id: {}", request.getId());
        Cinema cinema = hall.getCinema();
        if(!request.getName().equals(hall.getName())
                && cinemaHallRepository.existsByNameAndCinema(request.getName(), cinema)
        ){
            throw new BadRequestException("Duplicate name");
        }

        hall.setName(request.getName());
        hall.setType(request.getType());

        return cinemaHallMapper.toCinemaHallResponse(cinemaHallRepository.save(hall));
    }

    @Override
    public void delete() {

    }

    @Override
    public CinemaHallResponse getCinemaHall(Long cinemaHallId) {
        log.info("Find cinema hall with id {}", cinemaHallId);
        return cinemaHallMapper.toCinemaHallResponse(getById(cinemaHallId));
    }

    @Override
    public List<CinemaHallResponse> geyByCinema(Long cinemaId) {
        log.info("Find all cinema hall of cinema {}", cinemaId);

        Cinema cinema = cinemaService.getById(cinemaId);

        List<CinemaHall> halls = cinemaHallRepository.findAllByCinema(cinema);
        return halls.stream().map(cinemaHallMapper::toCinemaHallResponse).toList();
    }

    public CinemaHall getById(Long id){
        return cinemaHallRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cinema hall not found"));
    }
}
