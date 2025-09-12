package tech.pierandrei.StreamPix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StreamPixApplication {

	public static void main(String[] args) {
		SpringApplication.run(StreamPixApplication.class, args);
	}

}
