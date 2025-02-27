package vn.thachnn.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.thachnn.common.DayType;
import vn.thachnn.common.RoomType;
import vn.thachnn.common.SeatType;
import vn.thachnn.common.ShowtimeType;
import vn.thachnn.dto.request.TicketPriceRequest;
import vn.thachnn.dto.response.TicketPriceResponse;
import vn.thachnn.exception.BadRequestException;
import vn.thachnn.exception.ResourceNotFoundException;
import vn.thachnn.mapper.TicketPriceMapper;
import vn.thachnn.model.Showtime;
import vn.thachnn.model.TicketPrice;
import vn.thachnn.repository.TicketPriceRepository;
import vn.thachnn.repository.specification.TicketPriceSpecification;
import vn.thachnn.service.TicketPriceService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "TICKET-PRICE-SERVICE")
public class TicketPriceServiceImpl implements TicketPriceService {

    private final TicketPriceRepository ticketPriceRepository;
    private final TicketPriceMapper ticketPriceMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TicketPriceResponse create(TicketPriceRequest request) {
        log.info("Creating new TicketPrice: {}", request);

        if(ticketPriceRepository.existsByAttributes(
                request.getSeatType(), request.getRoomType(),
                request.getDayType(), request.getShowtimeType())
        ){
            throw new BadRequestException("A ticket price with the specified seat type, room type, day type, and showtime type already exists");
        }
        TicketPrice ticketPrice = ticketPriceMapper.toTicketPrice(request);
        ticketPrice = ticketPriceRepository.save(ticketPrice);
        return ticketPriceMapper.toTicketPriceResponse(ticketPrice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TicketPriceResponse update(Long ticketPriceId, Integer price) {
        log.info("Updating price with TicketPrice Id: {}", ticketPriceId);

        TicketPrice ticketPrice = getById(ticketPriceId);
        ticketPrice.setPrice(price);
        ticketPrice = ticketPriceRepository.save(ticketPrice);
        return ticketPriceMapper.toTicketPriceResponse(ticketPrice);
    }

    @Override
    public TicketPriceResponse getTicketPrice(Long id) {
        TicketPrice ticketPrice = getById(id);
        return ticketPriceMapper.toTicketPriceResponse(ticketPrice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("Deleting TicketPrice with id: {}", id);
        TicketPrice ticketPrice = getById(id);
        ticketPriceRepository.delete(ticketPrice);
    }

    @Override
    public Page<TicketPrice> getAll(ShowtimeType showtimeType, DayType dayType, RoomType roomType, SeatType seatType,
                       int pageNumber,int pageSize) {
        log.info("Get list TicketPrice");

        Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
        Specification<TicketPrice> specification =
                Specification.where(TicketPriceSpecification.hasRoomType(roomType))
                        .and(TicketPriceSpecification.hasSeatType(seatType))
                        .and(TicketPriceSpecification.hasDayType(dayType))
                        .and(TicketPriceSpecification.hasShowtimeType(showtimeType));

        return ticketPriceRepository.findAll(specification, pageable);
    }

    public TicketPrice getById(Long id){
        log.info("Find TicketPrice with id: {}", id);
        return ticketPriceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket Price not found"));
    }

    //get price by attributes
    @Cacheable(value = "price", key = "#showtimeType.name() + '-' + #roomType.name() + '-' + #seatType.name() + '-' + #dayType.name()")
    public Integer getPriceByAttributes(ShowtimeType showtimeType, RoomType roomType, SeatType seatType, DayType dayType){
        Specification<TicketPrice> specification =
                Specification.where(TicketPriceSpecification.hasRoomType(roomType))
                        .and(TicketPriceSpecification.hasSeatType(seatType))
                        .and(TicketPriceSpecification.hasDayType(dayType))
                        .and(TicketPriceSpecification.hasShowtimeType(showtimeType));

        List<TicketPrice> ticketPrices = ticketPriceRepository.findAll(specification);
        if (ticketPrices.get(0) != null){
            return ticketPrices.get(0).getPrice();
        }
        throw new ResourceNotFoundException("Price not found");
    }
}
