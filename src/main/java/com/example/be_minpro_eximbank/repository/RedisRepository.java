package com.example.be_minpro_eximbank.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisRepository {
    private static final String STRING_KEY_PREFIX = "mini-project:jwt:strings:" ;
    private final ValueOperations<String, String> valueOps;

    public RedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.valueOps = redisTemplate.opsForValue();
    }

    public void saveJwtKey(String username, String jwtKey) {
        valueOps.set(STRING_KEY_PREFIX + username, jwtKey, 1, TimeUnit.HOURS);
    }

    public String getJwtKey(String username) {
        return valueOps.get(STRING_KEY_PREFIX + username);
    }

    public void deleteJwtKey(String username) {
        valueOps.getOperations().delete(STRING_KEY_PREFIX + username);
    }
}

