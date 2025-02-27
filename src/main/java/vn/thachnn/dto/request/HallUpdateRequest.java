package vn.thachnn.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import vn.thachnn.common.RoomType;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HallUpdateRequest implements Serializable {

    private Long id;

    @Schema(description = "The name of the hall", example = "Screen01")
    @NotBlank(message = "name must be not blank")
    private String name;

    @Schema(description = "The type of the room", implementation = RoomType.class)
    private RoomType type;
}
