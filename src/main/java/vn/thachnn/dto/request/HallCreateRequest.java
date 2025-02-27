package vn.thachnn.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "The ID of the cinema where the hall is located", example = "10")
    @NotNull(message = "cinemaId must be not null")
    private Long cinemaId;

    @Schema(description = "The name of the hall", example = "Screen01")
    @NotBlank(message = "name must be not blank")
    private String name;

    @Schema(description = "The number of rows in the hall", example = "12")
    @NotNull(message = "row must be not null")
    private Integer row;

    @Schema(description = "The number of columns in the hall", example = "15")
    @NotNull(message = "column must be not null")
    private Integer column;

    @Schema(description = "The type of the room", implementation = RoomType.class)
    private RoomType type;
}
