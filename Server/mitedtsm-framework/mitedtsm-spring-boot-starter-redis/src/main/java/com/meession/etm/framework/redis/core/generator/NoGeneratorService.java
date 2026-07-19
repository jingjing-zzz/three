package com.meession.etm.framework.redis.core.generator;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class NoGeneratorService {

    @Resource
    private SequenceGenerator sequenceGenerator;

    public String generateOrderNo() {
        return sequenceGenerator.generate("order", "DD");
    }

    public String generateContractNo() {
        return sequenceGenerator.generate("contract", "HT");
    }

    public String generateReceivableNo() {
        return sequenceGenerator.generate("receivable", "HK");
    }

    public String generate(String key) {
        return sequenceGenerator.generate(key);
    }

    public String generate(String key, String prefix) {
        return sequenceGenerator.generate(key, prefix);
    }

    public String generate(String key, String prefix, int length) {
        return sequenceGenerator.generate(key, prefix, length);
    }
}