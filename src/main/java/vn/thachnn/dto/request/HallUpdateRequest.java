package vn.thachnn.dto.request;

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

    @NotBlank(message = "name must be not blank")
    private String name;

    private RoomType type;
}
