package vn.thachnn.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import vn.thachnn.common.CinemaStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CinemaResponse implements Serializable {

    @Schema(description = "id",
            example = "1")
    private Long id;

    @Schema(example = "CGV Times City")
    private String name;

    @Schema(example = "Tầng 4, Mê Linh Plaza Hà Đông, Đường Tô Hiệu, P.Hà Cầu, Q.Hà Đông, TP.Hà Nội, Việt Nam")
    private String address;

    @Schema(example = "Hà Nội")
    private String city;

    @Schema(implementation = CinemaStatus.class)
    private CinemaStatus status;

    @Schema(example = "106.700981")
    private BigDecimal lng;

    @Schema(example = "10.776889")
    private BigDecimal lat;

    @Schema(example = "2025-03-02T14:30:00")
    private LocalDateTime createdAt;

    @Schema(example = "2025-03-05T18:45:00")
    private LocalDateTime updatedAt;
}
