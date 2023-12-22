package com.langworthytech.bytebistro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableMethodSecurity(securedEnabled = true)
public class KeycloakdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeycloakdemoApplication.class, args);
	}

}
