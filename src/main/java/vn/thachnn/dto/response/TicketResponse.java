package vn.thachnn.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TicketResponse implements Serializable {

    private Long id;
    private Long showtimeId;
    private Integer totalAmount;
    private LocalDateTime createdAt;
    private String movieTitle;
    private String cinemaName;
    private String cinemaHallName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<JsonNode> details;

}
