package vn.thachnn.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewRequest implements Serializable {

    @NotBlank(message = "comment must be not blank")
    private String comment;

    @Min(value = 1, message = "rating must be at least 1")
    @Max(value = 10, message = "rating must be most 5")
    @NotNull(message = "rating must be not null")
    private Integer rating;

    @NotNull(message = "movieId must be not null")
    @Min(value = 1, message = "movieId must be greater than or equal to 1")
    private Long movieId;
}
