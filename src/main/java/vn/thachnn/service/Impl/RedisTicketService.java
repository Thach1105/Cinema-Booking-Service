package vn.thachnn.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vn.thachnn.exception.ResourceNotFoundException;
import vn.thachnn.model.RedisTicket;
import vn.thachnn.repository.RedisTicketRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "REDIS-TICKET-SERVICE")
public class RedisTicketService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTicketRepository redisTicketRepository;

    private final int TTL_IN_SECONDS = 310;

    public void create(RedisTicket redisTicket){
        redisTicketRepository.save(redisTicket);
    }

    public RedisTicket get(String id){
        return redisTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session Id not found or expired"));
    }

    public RedisTicket update(RedisTicket redisTicket){
        return redisTicketRepository.save(redisTicket);
    }

    public void delete(String id){
        redisTicketRepository.deleteById(id);
    }

    //Refresh the time for holding seats in Redis
    public void refreshTimeHeldSeat(RedisTicket redisTicket) throws JsonProcessingException {
        List<String> detail = redisTicket.getSeatDetail();
        for(var s : detail){
            JsonNode seat = new ObjectMapper().readTree(s);
            String key = "showtime:" + redisTicket.getShowtimeId() + ":seatId:" + seat.get("showtimeSeatId").asText();
            redisTemplate.expire(key, TTL_IN_SECONDS, TimeUnit.SECONDS);
        }
        String redisTicketKy = "RedisTicket:" + redisTicket.getId();
        redisTemplate.expire(redisTicketKy, TTL_IN_SECONDS, TimeUnit.SECONDS);
    }

    // Release the seats that are being held in a RedisTicket
    public void releaseHeldSeat(RedisTicket redisTicket)  {
        List<String> detail = redisTicket.getSeatDetail();

        for (var s : detail) {
            try {
                JsonNode seat = new ObjectMapper().readTree(s);
                String key = "showtime:" + redisTicket.getShowtimeId() + ":seatId:" + seat.get("showtimeSeatId").asText();

                // xóa các ghế có key tương ứng đang được giữ trong Redis
                redisTemplate.delete(key);
                log.info("Released hold for seat: {}", seat.get("seatNumber").asText());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        // Delete RedisTicket from Redis
        String redisTicketKey = "RedisTicket:" + redisTicket.getId();
        redisTemplate.delete(redisTicketKey);
        log.info("Deleted RedisTicket: {}", redisTicket.getId());
    }
}
