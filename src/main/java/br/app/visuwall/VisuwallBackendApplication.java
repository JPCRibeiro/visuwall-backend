package br.app.visuwall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class VisuwallBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(VisuwallBackendApplication.class, args);
	}

}
