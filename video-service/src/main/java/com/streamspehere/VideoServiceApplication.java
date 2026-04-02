package com.streamspehere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VideoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoServiceApplication.class, args);
		System.out.println("<== =========== VIDEO SERVICE STARTED ============ ==>");
	}

}
