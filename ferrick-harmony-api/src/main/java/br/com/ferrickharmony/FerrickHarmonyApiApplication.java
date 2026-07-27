package br.com.ferrickharmony;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FerrickHarmonyApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FerrickHarmonyApiApplication.class, args);
	}

}
