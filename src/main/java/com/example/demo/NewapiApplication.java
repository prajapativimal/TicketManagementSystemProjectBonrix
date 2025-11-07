package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // Add this annotation

public class NewapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewapiApplication.class, args);
	}

}
