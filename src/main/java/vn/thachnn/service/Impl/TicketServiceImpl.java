package vn.thachnn.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.thachnn.exception.ResourceNotFoundException;
import vn.thachnn.model.Showtime;
import vn.thachnn.model.Ticket;
import vn.thachnn.model.TicketDetail;
import vn.thachnn.model.User;
import vn.thachnn.repository.TicketDetailRepository;
import vn.thachnn.repository.TicketRepository;
import vn.thachnn.service.TicketService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "TICKET-SERVICE")
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketDetailRepository ticketDetailRepository;
    private final ShowtimeServiceImpl showtimeService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ShowtimeSeatsServiceImpl showtimeSeatsService;
    private final PaymentServiceImpl paymentService;

    @KafkaListener(topics = "ticket-payment-success", groupId = "create-ticket-group")
    @Transactional(rollbackFor = Exception.class)
    public Ticket createNewTicketByKafka(String message) {
        log.info("Create new ticket {}", message);

        List<JsonNode> seatList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode redisTicket;

        //convert message from String to JsonNode
        try {
            redisTicket = objectMapper.readTree(message);
            JsonNode seatDetail = redisTicket.get("seatDetail");

            if(seatDetail.isArray()) {
                for (var detail : seatDetail) {
                    JsonNode seat = objectMapper.readTree(detail.asText());
                    seatList.add(seat);
                }
            }
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

        String transId = redisTicket.get("id").asText();
        Long showtimeId = redisTicket.get("showtimeId").asLong();
        int amount = redisTicket.get("totalAmount").asInt();
        Long userId = redisTicket.get("userId").asLong();

        // get showtime and user info
        Showtime showtime = showtimeService.getById(showtimeId);
        List<Long> seatIdList = new ArrayList<>();
        //create new ticket
        Ticket ticket = Ticket.builder()
                .userId(userId)
                .showtimeId(showtimeId)
                .movieTitle(showtime.getMovie().getTitle())
                .cinemaName(showtime.getHall().getCinema().getName())
                .cinemaHallName(showtime.getHall().getName())
                .startTime(showtime.getStartTime())
                .endTime(showtime.getEndTime())
                .totalAmount(amount)
                .build();

        //save new ticket to database
        ticket = ticketRepository.save(ticket);

        // create detail for new ticket
        List<TicketDetail> ticketDetailList = new ArrayList<>();
        for (var seat : seatList){

            Long showtimeSeatId = seat.get("showtimeSeatId").asLong();
            seatIdList.add(showtimeSeatId);

            TicketDetail ticketDetail = TicketDetail.builder()
                    .ticket(ticket)
                    .price(seat.get("price").asInt())
                    .seatType(seat.get("seatType").asText())
                    .seatNumber(seat.get("seatNumber").asText())
                    .showtimeSeat(showtimeSeatId)
                    .build();

            ticketDetailList.add(ticketDetail);

            //delete seat in redis
            String redisKey = "showtime:" + showtimeId + ":seatId:" + showtimeSeatId;
            redisTemplate.delete(redisKey);
        }
        //update status seat in database
        showtimeSeatsService.updateSeatStatusToBooked(seatIdList);

        //save ticket detail of new ticket to database
        ticketDetailRepository.saveAll(ticketDetailList);

        //save new payment for this ticket
        paymentService.createNewPayment(transId, ticket, amount);

        return ticket;
    }

    @Override
    public Ticket getById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
    }

    @Override
    public Page<Ticket> getListTicketForUser(User user, int pageNumber, int pageSize){
        log.info("Get list tickets for user id: {}", user.getId());

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize, sort);

        return ticketRepository.findAllByUserId(user.getId(), pageable);
    }
}
