package org.imperial.fastquantanalysis;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

public class RedisTest {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;



}
