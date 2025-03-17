package org.imperial.fastquantanalysis.util;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Globally unique ID generator
 *
 * @author Emil S. He
 * @since 2025-03-17
 */
@Slf4j
@Component
public class RedisIdUtil {

    // Beginning timestamp
    // 2025-02-11 00:00:00
    private static final long BEGIN_TIMESTAMP = 1739232000L;
    private static final int COUNT_BIT = 32;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public String nextId(String keyPrefix) {
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSecond -  BEGIN_TIMESTAMP;

        String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        long count = stringRedisTemplate.opsForValue().increment("icr:" + keyPrefix + ":" + date);
        long result = timestamp << COUNT_BIT | count;

        return String.valueOf(result);
    }
}
