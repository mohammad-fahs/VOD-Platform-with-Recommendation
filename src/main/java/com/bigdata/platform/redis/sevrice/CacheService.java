package com.bigdata.platform.redis.sevrice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final RedisTemplate<String, List<Long>> redisTemplate;

    // Add a field to the cache
    public void addToCache(String key, List<Long> value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // Check if a field is contained in the cache
    public boolean isFieldInCache(String key) {
        return redisTemplate.hasKey(key);
    }

    // Return cached field
    public List<Long> getCachedField(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
