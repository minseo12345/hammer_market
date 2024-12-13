package com.hammer.hammer.chat.mongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MongoDBConnectionTest implements CommandLineRunner {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            System.out.println("Connected to MongoDB! Databases:");
            mongoTemplate.getDb().listCollectionNames().forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("MongoDB connection failed: " + e.getMessage());
        }
    }
}
