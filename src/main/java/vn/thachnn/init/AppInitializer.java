package vn.thachnn.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppInitializer implements CommandLineRunner {

    private final InitService initService;

    @Override
    public void run(String... args) throws Exception {
        initService.initializeRole();
        initService.initializeUser();
    }
}
