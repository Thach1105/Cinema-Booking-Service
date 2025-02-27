package vn.thachnn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // Số lượng thread tối thiểu
        executor.setMaxPoolSize(5);  // Số lượng thread tối đa
        executor.setQueueCapacity(100); // Số lượng tác vụ tối đa trong hàng đợi
        executor.setThreadNamePrefix("async-thread-"); // Tiền tố cho tên thread
        executor.initialize();
        return executor;
    }
}
