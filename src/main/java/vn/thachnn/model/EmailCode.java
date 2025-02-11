package vn.thachnn.model;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "email_code", timeToLive = 300L)
public class EmailCode implements Serializable {

    @Id
    private String id;
    private String email;
}
