package tech.pierandrei.StreamPix.SmtpEmail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SmtpEmailApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmtpEmailApplication.class, args);
	}

}
