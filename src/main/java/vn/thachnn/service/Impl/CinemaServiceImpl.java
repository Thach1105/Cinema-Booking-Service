package vn.thachnn.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.thachnn.common.CinemaStatus;
import vn.thachnn.dto.request.CinemaRequest;
import vn.thachnn.dto.response.CinemaResponse;
import vn.thachnn.exception.ResourceNotFoundException;
import vn.thachnn.mapper.CinemaMapper;
import vn.thachnn.model.Cinema;
import vn.thachnn.repository.CinemaRepository;
import vn.thachnn.service.CinemaService;

@Service
@Slf4j(topic = "CINEMA-SERVICE")
@RequiredArgsConstructor
public class CinemaServiceImpl implements CinemaService {

    private final CinemaMapper cinemaMapper;
    private final CinemaRepository cinemaRepository;

    @Override
    @Transactional
    public CinemaResponse create(CinemaRequest request) {
        log.info("Creating cinema: {}", request);
        Cinema cinema = cinemaMapper.toCinema(request);
        cinema.setStatus(CinemaStatus.NONE);

        Cinema newCinema = cinemaRepository.save(cinema);
        log.info("Created cinema with id {}", newCinema.getId());

        return cinemaMapper.toCinemaResponse(newCinema);
    }

    @Override
    @Transactional
    public CinemaResponse update(Long cinemaId,CinemaRequest request) {
        log.info("Updating cinema id: {}", cinemaId);
        Cinema cinema = getById(cinemaId);

        cinema.setStatus(request.getStatus());
        cinema.setName(request.getName());
        cinema.setAddress(request.getAddress());
        cinema.setLng(request.getLng());
        cinema.setLat(request.getLat());

        Cinema cinemaUpdated = cinemaRepository.save(cinema);
        log.info("Updated cinema id {}", cinemaUpdated.getId());

        return cinemaMapper.toCinemaResponse(cinemaUpdated);
    }

    @Override
    public Cinema getById(Long cinemaId) {
        return cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cinema not found"));
    }

    @Override
    public Page<Cinema> getList(String city, Integer pageNumber, Integer pageSize) {
        log.info("find all cinema start");
        Sort.Order orderByCity = new Sort.Order(Sort.Direction.ASC, "city");

        Pageable pageable = PageRequest.of(pageNumber-1, pageSize, Sort.by(orderByCity));
        Page<Cinema> cinemaPage;
        if(StringUtils.hasLength(city)){
            cinemaPage = cinemaRepository.searchByCity("%"+city+"%", pageable);
        } else {
            cinemaPage = cinemaRepository.findAll(pageable);
        }

        return cinemaPage;
    }

    @Override
    public void delete(Long cinemaId) {
        log.info("Deleting cinema id: {}", cinemaId);
        Cinema cinema = getById(cinemaId);
        cinema.setStatus(CinemaStatus.INACTIVE);

        //cinemaRepository.save(cinema);
        cinemaRepository.delete(cinema);
    }

    public CinemaResponse getCinema(Long cinemaId){
        return cinemaMapper.toCinemaResponse(getById(cinemaId));
    }

}
