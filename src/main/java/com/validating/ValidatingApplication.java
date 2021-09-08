package com.validating;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@EnableBatchProcessing
public class ValidatingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ValidatingApplication.class, args);
	}

}
