package com.hammer.hammer.global.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 데이터 저장
    public void save(String key, Object value, long timeoutInSeconds) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(timeoutInSeconds));
    }

    // 데이터 조회
    public Object find(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 데이터 삭제
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}

