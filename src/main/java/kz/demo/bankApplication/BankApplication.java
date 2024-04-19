package kz.demo.bankApplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Bank Application",
				description = "Backend REST API's for Bank App",
				version = "v1.0",
				contact = @Contact(
						name = "Abai Amangeldiuly",
						email = "abai.45.abai@gmail.com",
						url = "https://github.com/abai45/bankApplication"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "Bank Application Spring Boot",
				url = "https://github.com/abai45/bankApplication"
		)
)
public class BankApplication {
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(BankApplication.class, args);
	}

}
