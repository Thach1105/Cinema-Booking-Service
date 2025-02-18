package vn.thachnn.dto.request;

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

    @NotBlank(message = "name must be not blank")
    private String name;

    @NotBlank(message = "address must be not blank")
    private String address;

    @NotBlank(message = "city must be not blank")
    private String city;

    private CinemaStatus status;

    @NotNull(message = "longitude must be not null")
    private BigDecimal lng;

    @NotNull(message = "latitude must be not null")
    private BigDecimal lat;
}
