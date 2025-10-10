package nexum.com.nexumbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class NexumbankApplication {

	public static void main(String[] args) {
		SpringApplication.run(NexumbankApplication.class, args);
	}

}
