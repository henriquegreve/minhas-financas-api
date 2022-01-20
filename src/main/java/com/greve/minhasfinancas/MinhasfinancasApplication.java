package com.greve.minhasfinancas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@SpringBootApplication
public class MinhasfinancasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MinhasfinancasApplication.class, args);
	}
}
