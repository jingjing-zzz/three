package com.meession.etm.framework.redis.core.generator;

public interface SequenceGenerator {

    String generate(String key);

    String generate(String key, String prefix);

    String generate(String key, String prefix, int length);
}