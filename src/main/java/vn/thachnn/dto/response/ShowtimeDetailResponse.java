package vn.thachnn.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShowtimeDetailResponse implements Serializable {

    private Long id;

    @JsonProperty("movie_name")
    private String movieName;

    @JsonProperty("cinema_name")
    private String cinemaName;

    @JsonProperty("hall_name")
    private String cinemaHallName;
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer price;
}
