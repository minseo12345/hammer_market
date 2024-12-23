package com.hammer.hammer.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

@Configuration
public class MongoIndexConfig {

    public MongoIndexConfig(MongoTemplate mongoTemplate) {
        IndexOperations indexOps = mongoTemplate.indexOps("message"); // 컬렉션 이름
        indexOps.ensureIndex(
                new Index().on("createdAt", org.springframework.data.domain.Sort.Direction.ASC)
                        .expire(2592000L) // 2592000L 30일
        );
        IndexOperations chatRoomIndexOps = mongoTemplate.indexOps("chatRoom");
        chatRoomIndexOps.ensureIndex(
                new Index().on("updatedAt", org.springframework.data.domain.Sort.Direction.ASC)
                        .expire(7776000L) // 7776000L 90일
        );
    }
}