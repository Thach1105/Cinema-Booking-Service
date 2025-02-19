package vn.thachnn.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import vn.thachnn.common.RoomType;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HallCreateRequest implements Serializable {

    private Long id;

    @NotNull(message = "cinemaId must be not null")
    private Long cinemaId;

    @NotBlank(message = "name must be not blank")
    private String name;

    @NotNull(message = "row must be not null")
    private Integer row;

    @NotNull(message = "column must be not null")
    private Integer column;

    private RoomType type;
}
