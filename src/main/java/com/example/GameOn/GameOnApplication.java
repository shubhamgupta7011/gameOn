package com.example.GameOn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class GameOnApplication {

	public static void main(String[] args) {
		SpringApplication.run(GameOnApplication.class, args);
	}

}
