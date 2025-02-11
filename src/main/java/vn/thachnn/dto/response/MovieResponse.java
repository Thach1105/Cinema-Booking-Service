package vn.thachnn.dto.response;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieResponse implements Serializable {

    private Long id;
    private String title;
    private String description;
    private Integer duration;
    private Integer ageLimit;
    private LocalDate releaseDate;
}
