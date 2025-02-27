package vn.thachnn.model;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "RedisTicket", timeToLive = 310L)
@ToString
public class RedisTicket implements Serializable {

    @Id
    private String id;  //transaction code

    private Long showtimeId;
    private Long userId;
    private Integer totalAmount;
    private List<String> seatDetail;
    private boolean hasGenerated;
    private boolean paid;
}
