package vn.thachnn.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import vn.thachnn.common.MovieStatus;
import vn.thachnn.model.Movie;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MovieSpecification {

    public static Specification<Movie> hasTitle(String title){
        return (root, query, criteriaBuilder) ->
                title == null ? null : criteriaBuilder.like(root.get("title"), "%"+title+"%");
    }

    public static Specification<Movie> hasStatus(MovieStatus status){
        return (root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Movie> hasReleaseDate(LocalDate releaseDate){
        return (root, query, criteriaBuilder) ->
                releaseDate == null ? null : criteriaBuilder.equal(root.get("releaseDate"), releaseDate);
    }

    public static Specification<Movie> hasDuration(Integer minDuration, Integer maxDuration) {
        return (root, query, criteriaBuilder) -> {
            if (minDuration == null && maxDuration == null) return null;
            if (minDuration != null && maxDuration != null)
                return criteriaBuilder.between(root.get("duration"), minDuration, maxDuration);
            if (minDuration != null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get("duration"), minDuration);
            return criteriaBuilder.lessThanOrEqualTo(root.get("duration"), maxDuration);
        };
    }

    public static Specification<Movie> hasAgeLimit(String ageLimitCondition){
        return (root, query, criteriaBuilder) -> {
            if(!StringUtils.hasLength(ageLimitCondition)) return null;

            Pattern pattern = Pattern.compile("([<>=]*)(\\d+)");
            Matcher matcher = pattern.matcher(ageLimitCondition);

            if (matcher.matches()){
                String operator = matcher.group(1);
                int value = Integer.parseInt(matcher.group(2));

                if (!StringUtils.hasLength(operator)){
                    return criteriaBuilder.equal(root.get("ageLimit"), value);
                }

                switch (operator){
                    case "<":
                        return criteriaBuilder.lessThan(root.get("ageLimit"), value);

                    case ">":
                        return criteriaBuilder.greaterThan(root.get("ageLimit"), value);

                    case "=":
                        return criteriaBuilder.equal(root.get("ageLimit"), value);

                    case "<=":
                        return criteriaBuilder.lessThanOrEqualTo(root.get("ageLimit"), value);

                    case ">=":
                        return criteriaBuilder.greaterThanOrEqualTo(root.get("ageLimit"), value);
                }
            }

            return null;
        };
    }
}
