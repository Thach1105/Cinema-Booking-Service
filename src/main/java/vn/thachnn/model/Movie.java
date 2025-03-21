package vn.thachnn.model;

import jakarta.persistence.*;
import lombok.*;
import vn.thachnn.common.MovieStatus;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "movie")
public class Movie {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", length = 512, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "age_limit", nullable = false)
    private Integer ageLimit;

    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @Column(name = "trailer")
    private String trailer;

    @Column(name = "banner", unique = true)
    private String banner;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MovieStatus status;
}
