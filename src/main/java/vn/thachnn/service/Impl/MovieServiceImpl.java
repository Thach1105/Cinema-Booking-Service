package vn.thachnn.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import vn.thachnn.common.MovieStatus;
import vn.thachnn.dto.request.MovieRequest;
import vn.thachnn.dto.response.MovieResponse;
import vn.thachnn.exception.BadRequestException;
import vn.thachnn.exception.ResourceNotFoundException;
import vn.thachnn.mapper.MovieMapper;
import vn.thachnn.model.Movie;
import vn.thachnn.repository.MovieRepository;
import vn.thachnn.repository.ShowtimeRepository;
import vn.thachnn.repository.specification.MovieSpecification;
import vn.thachnn.service.CloudinaryService;
import vn.thachnn.service.MovieService;

import java.time.Instant;
import java.time.LocalDate;

@Service
@Slf4j(topic = "MOVIE-SERVICE")
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final CloudinaryService cloudinaryService;
    private final ShowtimeRepository showtimeRepository;

    @Value("${cloudinary.cloud-name}")
    private String CLOUD_NAME;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MovieResponse save(MovieRequest request, MultipartFile movieBanner) {
        log.info("Saving movie: {}", request);

        //convert MovieRequest DTO to Movie
        Movie movie = movieMapper.toMovie(request);
        if(movie.getReleaseDate().isAfter(LocalDate.now())){
            movie.setStatus(MovieStatus.COMING_UP);
        } else movie.setStatus(MovieStatus.NONE);

        //create banner name to upload
        String nameFile = String.valueOf(Instant.now().toEpochMilli());
        String bannerUrl = String.format("https://res.cloudinary.com/%s/image/upload/movie/%s.jpg", CLOUD_NAME, nameFile);
        movie.setBanner(bannerUrl);
        movie = movieRepository.save(movie);
        log.info("Created new movie with id {}", movie.getId());

        cloudinaryService.uploadBannerForMovie(movieBanner, nameFile, movie);
        return movieMapper.toMovieResponse(movie);
    }

    @Override
    @Cacheable(value = "movieCache", key = "#id")
    public MovieResponse findById(Long id) {
        log.info("Find movie by id: {}", id);
        Movie movie = getMovie(id);
        return movieMapper.toMovieResponse(movie);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CachePut(value = "movieCache", key = "#id")
    public MovieResponse update(MovieRequest request, Long movieId) {
        log.info("Updating movie: {}", request);

        //Get movie by id
        Movie movie = getMovie(movieId);
        Movie updatedMovie = movieMapper.toMovie(request);
        updatedMovie.setId(movieId);
        updatedMovie.setBanner(movie.getBanner());

        //update movie in database
        movieRepository.save(updatedMovie);
        return movieMapper.toMovieResponse(updatedMovie);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMovieBanner(Long movieId, MultipartFile newBanner) {
        log.info("Update movie banner");
       try {
           Movie movie = getMovie(movieId);

           //get current url and publicId
           String currentUrl = movie.getBanner();
           String publicIdBanner = "";
           if(StringUtils.hasLength(currentUrl)){
               publicIdBanner = currentUrl.substring(
                       currentUrl.lastIndexOf("/") + 1, currentUrl.lastIndexOf("."));
           }
           String bannerName = String.valueOf(Instant.now().toEpochMilli());

           // upload new banner to Cloudinary and delete old banner
           byte[] file = newBanner.getBytes();
           String bannerUrl = cloudinaryService.uploadFile(file, "movie", bannerName);
           if(StringUtils.hasLength(publicIdBanner)) cloudinaryService.deleteFile("movie/"+publicIdBanner);

           movie.setBanner(bannerUrl);
           movieRepository.save(movie);
       } catch (Exception e){
           log.error(e.getMessage());
            throw new BadRequestException("Upload file failed");
       }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMovieStatus(Long movieId, MovieStatus status) {
        log.info("Updating movie status");

        Movie movie = getMovie(movieId);
        if(status.equals(MovieStatus.NONE)){
            int count = showtimeRepository.countShowtimeOfMovieFuture(movie);
            if (count > 0) throw new BadRequestException("Cannot change movie status to 'NONE'" +
                    " because there are still upcoming showtimes.");
        }

        movie.setStatus(status);
        movieRepository.save(movie);
    }

    @Override
    public Page<Movie> getListMoviePublic(int pageNumber, int pageSize, MovieStatus status) {
        Sort sort = status == MovieStatus.NOW_SHOWING
                ? Sort.by(Sort.Direction.DESC, "releaseDate")
                : Sort.by(Sort.Direction.ASC, "releaseDate");

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        return movieRepository.getListMoviePublic(status, pageable);
    }

    @Override
    public Page<Movie> findAll(
            String title, LocalDate releaseDate, Integer minDuration, Integer maxDuration,
            String ageLimitCondition, MovieStatus status,
            String sortBy, String direction, int pageNumber, int pageSize
    ){
        log.info("Find list movie start: ");
        Specification<Movie> spec = Specification.where(MovieSpecification.hasTitle(title))
                .and(MovieSpecification.hasReleaseDate(releaseDate))
                .and(MovieSpecification.hasDuration(minDuration, maxDuration))
                .and(MovieSpecification.hasAgeLimit(ageLimitCondition))
                .and(MovieSpecification.hasStatus(status));

        Sort sort;
        if(!StringUtils.hasLength(sortBy)){
            sort = Sort.unsorted();
        } else {
            sort = "desc".equalsIgnoreCase(direction)
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
        }

        Pageable pageable = PageRequest.of(pageNumber -1, pageSize, sort);
        return movieRepository.findAll(spec, pageable);
    }

    @Override
    public Movie getMovie(Long id){
        return movieRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Movie not found")
        );
    }
}
