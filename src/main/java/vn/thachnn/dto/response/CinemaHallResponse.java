package vn.thachnn.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.thachnn.common.RoomType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CinemaHallResponse implements Serializable {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CinemaSimpleResponse cinema;
    private String name;
    private Integer row;
    private Integer column;
    private Integer totalSeats;
    private RoomType type;
}
