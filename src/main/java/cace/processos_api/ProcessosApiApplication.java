package cace.processos_api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "cace.processos_api.model")
@OpenAPIDefinition(info = @Info(title = "API-Processos" , version = "1" , description = "API que gerencia todos os processos que estão sobre o controle da CACE"))
@EnableJpaRepositories(basePackages = "cace.processos_api.repository")
@EnableCaching
public class ProcessosApiApplication {

	//Database
	@Value("${spring.datasource.url:NOT_DEFINED}")
	private String dbUrl;


	//Email
	@Value("${spring.mail.host:NOT_DEFINED}")
	private String mailHost;
	@Value("${spring.mail.port:NOT_DEFINED}")
	private String mailPort;
	@Value("${spring.mail.username:NOT_DEFINED}")
	private String mailUsername;
	@Value("${spring.mail.properties.mail.smtp.auth:true}")
	private String mailSmtpAuth;
	@Value("${spring.mail.properties.mail.smtp.starttls.enable:true}")
	private String mailSmtpStarttlsEnable;


	//Redis
	@Value("${spring.data.redis.host:NOT_DEFINED}")
	private String redisHost;
	@Value("${spring.data.redis.port:NOT_DEFINED}")
	private String redisPort;
	@Value("${spring.data.redis.ssl-enabled:NOT_DEFINED}")
	private String redisSslEnabled;




	public static void main(String[] args) {
		SpringApplication.run(ProcessosApiApplication.class, args);
	}

	@PostConstruct
	public void logEnv() {
		System.out.println("\n==== DATABASE_URL ====");
		System.out.println("spring.datasource.url: " + dbUrl);
		System.out.println("======================");


		System.out.println("\n==== Email ENV ====");
		System.out.println("Host: " + mailHost);
		System.out.println("Port: " + mailPort);
		System.out.println("Username: " + mailUsername);
		System.out.println("SMTP Auth: " + mailSmtpAuth);
		System.out.println("SMTP StartTLS Enabled: " + mailSmtpStarttlsEnable);


		System.out.println("\n==== Redis ENV ====");
		System.out.println("Host: " + redisHost );
		System.out.println("Port: " + redisPort);
		System.out.println("SSL Enabled: " + redisSslEnabled);
		System.out.println("===================\n");
	}
}
