package booktrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing

public class BookTrackApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookTrackApplication.class, args);
    }

}
