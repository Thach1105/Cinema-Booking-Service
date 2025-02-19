package vn.thachnn.dto.response;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ShowtimeResponse implements Serializable {

    private Long id;
    private Long movieId;
    private String movieName;
    private Long hallId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer price;
}
