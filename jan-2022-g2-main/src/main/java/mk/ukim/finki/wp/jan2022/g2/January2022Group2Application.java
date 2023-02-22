package mk.ukim.finki.wp.jan2022.g2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class January2022Group2Application {

	public static void main(String[] args) {
		SpringApplication.run(January2022Group2Application.class, args);

	}

	@Bean
	PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder(10);
	}

}
