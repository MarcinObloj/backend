package com.example.cv2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
@SpringBootApplication
@EntityScan("com.example.cv2.model")
public class Cv2Application {

	public static void main(String[] args) {
		SpringApplication.run(Cv2Application.class, args);
	}

}
