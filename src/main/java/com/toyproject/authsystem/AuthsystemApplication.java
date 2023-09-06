package com.toyproject.authsystem;

import com.toyproject.authsystem.service.FileStoreProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		FileStoreProperties.class
})
public class AuthsystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthsystemApplication.class, args);
	}

}
