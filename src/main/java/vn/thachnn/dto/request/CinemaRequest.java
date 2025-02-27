package vn.thachnn.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import vn.thachnn.common.CinemaStatus;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CinemaRequest implements Serializable {

    @Schema(description = "The name of the cinema", example = "CGV Times City")
    @NotBlank(message = "name must be not blank")
    private String name;

    @Schema(description = "The address of the cinema", example = "Tầng 4, Mê Linh Plaza Hà Đông, Đường Tô Hiệu, P.Hà Cầu, Q.Hà Đông, TP.Hà Nội, Việt Nam")
    @NotBlank(message = "address must be not blank")
    private String address;

    @Schema(description = "The city where the cinema is located", example = "Tp. Hà Nội")
    @NotBlank(message = "city must be not blank")
    private String city;

    @Schema(description = "The status of the cinema", implementation = CinemaStatus.class)
    private CinemaStatus status;

    @Schema(description = "The longitude of the cinema", example = "105.8542")
    @NotNull(message = "longitude must be not null")
    private BigDecimal lng;

    @Schema(description = "The latitude of the cinema", example = "21.0285")
    @NotNull(message = "latitude must be not null")
    private BigDecimal lat;
}
