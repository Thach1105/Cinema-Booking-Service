package vn.thachnn.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.thachnn.exception.BadRequestException;
import vn.thachnn.model.Movie;
import vn.thachnn.repository.MovieRepository;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CLOUDINARY-SERVICE")
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private final MovieRepository movieRepository;

    @Value("${cloudinary.cloud-name}")
    private String CLOUD_NAME;

    @Async("taskExecutor")
    public void uploadBannerForMovie(
            MultipartFile banner, String fileName, Movie movie){

        try {
            byte[] fileBytes = banner.getBytes();
            String bannerUrl = uploadFile(fileBytes, "movie", fileName);
            movie.setBanner(bannerUrl);
            movieRepository.save(movie);
        } catch (IOException e) {
            log.warn(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }

    // upload file to Cloudinary
    public String uploadFile(byte[] file, String folderName, String fileName) {
        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "folder", folderName,
                "public_id", fileName
        );
        try {
            Map uploadResult = cloudinary.uploader().upload(file, uploadParams);

            String publicId = uploadResult.get("public_id").toString();
            String url = String.format("https://res.cloudinary.com/%s/image/upload/%s.jpg", CLOUD_NAME, publicId);
            log.info("Upload file successfully with url: {}", url);
            return url;
        } catch (IOException e){
            log.error("Upload failed, error {}", e.getMessage());
            throw new BadRequestException("Upload file failed");
        }
    }

    //delete file
    public void deleteFile(String publicId){
        try {
            Map<String, Object> deleteParams = ObjectUtils.asMap(
                    "invalidate", true
            );
            Map result = cloudinary.uploader().destroy(publicId, deleteParams);
            log.info("Deleted file from Cloudinary with publicId: {}", publicId);
        } catch (Exception e) {
            log.error("Failed to delete file from Cloudinary, error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
