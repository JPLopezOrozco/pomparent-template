package com.juan.monolithapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MonolithAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonolithAppApplication.class, args);
	}

}
