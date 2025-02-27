package vn.thachnn.dto.response;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResponse implements Serializable {

    private Long id;
    private Integer rating;
    private String comment;
    private String userFullName;
    private String movieName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}