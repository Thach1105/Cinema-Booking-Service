package vn.thachnn.dto.response;

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

    private Long id;
    private String name;
    private String address;
    private String city;
    private CinemaStatus status;
    private BigDecimal lng;
    private BigDecimal lat;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
