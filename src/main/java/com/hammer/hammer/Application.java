package com.hammer.hammer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//오토컨피그
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)//오토컨피그
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
