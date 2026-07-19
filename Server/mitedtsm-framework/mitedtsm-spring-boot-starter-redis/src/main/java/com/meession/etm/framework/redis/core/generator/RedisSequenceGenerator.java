package com.meession.etm.framework.redis.core.generator;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class RedisSequenceGenerator implements SequenceGenerator {

    private static final int DEFAULT_LENGTH = 6;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String generate(String key) {
        return generate(key, "", DEFAULT_LENGTH);
    }

    @Override
    public String generate(String key, String prefix) {
        return generate(key, prefix, DEFAULT_LENGTH);
    }

    @Override
    public String generate(String key, String prefix, int length) {
        String datePart = DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATE_PATTERN);
        String redisKey = "seq:" + key + ":" + datePart;
        Long sequence = stringRedisTemplate.opsForValue().increment(redisKey);
        stringRedisTemplate.expire(redisKey, Duration.ofDays(1L));
        String formatPattern = "%0" + length + "d";
        return prefix + datePart + String.format(formatPattern, sequence);
    }
}