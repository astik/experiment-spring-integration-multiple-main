package fr.smile.poc.example0;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication()
public class Main {
	public static void main(String[] args) {
		String appName = "Example 0";
		log.trace("[{}] main", appName);
		SpringApplication.run(Main.class, args);
	}
}
