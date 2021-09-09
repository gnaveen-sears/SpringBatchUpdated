package com.validating;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ValidatingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ValidatingApplication.class, args);
	}

}
