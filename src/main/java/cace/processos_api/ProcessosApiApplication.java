package cace.processos_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "cace.processos_api.model")
@EnableJpaRepositories(basePackages = "cace.processos_api.repository")
public class ProcessosApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProcessosApiApplication.class, args);
	}

}