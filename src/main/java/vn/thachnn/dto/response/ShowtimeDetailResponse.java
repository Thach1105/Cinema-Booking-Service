package vn.thachnn.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShowtimeDetailResponse implements Serializable {

    private Long id;
    private String movieName;
    private String cinemaName;
    private String cinemaHallName;
    private Integer ageLimit;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    /*private Integer price;*/
    private List<ShowtimeSeatResponse> seats;
    private Integer availableSeats;
}
