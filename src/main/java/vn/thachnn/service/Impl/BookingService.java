package vn.thachnn.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vn.thachnn.common.SeatStatus;
import vn.thachnn.common.ShowtimeType;
import vn.thachnn.dto.request.TicketRequest;
import vn.thachnn.exception.BadRequestException;
import vn.thachnn.model.*;
import vn.thachnn.util.DateTimeUtils;
import vn.thachnn.util.TransCodeGenerator;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "BOOKING-SERVICE")
public class BookingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ShowtimeServiceImpl showtimeService;
    private final ShowtimeSeatsServiceImpl showtimeSeatsService;
    private final TicketPriceServiceImpl ticketPriceService;
    private final RedisTicketService redisTicketService;

    public String requestToBookTicket(TicketRequest request, User user){

        log.info("Start booking process for the user with id: {}", user.getId());

        Showtime showtime = showtimeService.getById(request.getShowtimeId());
        List<Long> seatIds = request.getSeatIds();
        List<ShowtimeSeats> seatsList = showtimeSeatsService.getAllByIds(seatIds);

        for (ShowtimeSeats ss : seatsList){
            SeatStatus status = SeatStatus.valueOf(showtimeSeatsService.isAvailable(showtime, ss));
            if(!status.equals(SeatStatus.AVAILABLE)){
                throw new BadRequestException(String.format("Seat %s is not available", ss.getSeat().getSeatNumber()));
            }
        }

        List<String> detail = new ArrayList<>();
        Integer totalAmount = 0;

        for (ShowtimeSeats ss : seatsList){
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            data.put("status", SeatStatus.PENDING);
            showtimeSeatsService.reservedSeatInRedis(showtime, ss, data);

            Integer seatPrice = ticketPriceService.getPriceByAttributes(
                    ShowtimeType.REGULAR,
                    showtime.getHall().getType(),
                    ss.getSeat().getType(),
                    DateTimeUtils.checkDayType(showtime.getStartTime()));

            JsonNode seat = new ObjectMapper().createObjectNode()
                    .put("showtimeSeatId", ss.getId())
                    .put("seatNumber", ss.getSeat().getSeatNumber())
                    .put("seatType", ss.getSeat().getType().toString())
                    .put("price", seatPrice);


            totalAmount += seatPrice;
            detail.add(seat.toString());
        }
        log.info("Hold tick for user {} successfully", user.getId());

        String transCode = TransCodeGenerator.generateTransCode("yyMMdd", 12);
        RedisTicket redisTicket = RedisTicket.builder()
                .id(transCode)
                .showtimeId(showtime.getId())
                .userId(user.getId())
                .totalAmount(totalAmount)
                .seatDetail(detail)
                .hasGenerated(false)
                .build();

        redisTicketService.create(redisTicket);
        return redisTicket.getId();
    }

    // check trước khi tạo ra link thanh toán
    public void checkBeforeCreatePayment(RedisTicket redisTicket, User user) throws JsonProcessingException {

        if(redisTicket.isHasGenerated()) {
            throw new BadRequestException("This payment session has already been assigned a payment link");
        }

        if(!redisTicket.getUserId().equals(user.getId())) {
            throw new BadRequestException("You do not have permission to pay for this sessionId");
        }

        List<String> detail = redisTicket.getSeatDetail();
        for(var s : detail) {
            JsonNode seat = new ObjectMapper().readTree(s);
            String key = "showtime:" + redisTicket.getShowtimeId() + ":seatId:" + seat.get("showtimeSeatId").asText();
            Map<Object, Object> seatData = redisTemplate.opsForHash().entries(key);
            Long userId = (Long) seatData.get("userId");
            if(!seatData.isEmpty() && !Objects.equals(userId, user.getId())){
                throw new BadRequestException("This seat is not reserved for you.");
            }
        }
    }
}
