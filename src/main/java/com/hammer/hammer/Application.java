package com.hammer.hammer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableJpaRepositories(basePackages = {"com.hammer.hammer.bid.repository",
		"com.hammer.hammer.item.repository","com.hammer.hammer.transaction.repository",
		"com.hammer.hammer.user.repository"})
@EntityScan(basePackages = {"com.hammer.hammer.bid.entity",
		"com.hammer.hammer.item.entity","com.hammer.hammer.transaction.entity",
		"com.hammer.hammer.user.entity"})
@EnableMongoRepositories(basePackages = {"com.hammer.hammer.chat.repository",
		"com.hammer.hammer.chat.entity"})
@EnableMongoAuditing
@EnableCaching
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
