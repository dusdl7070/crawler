package com.me.crawler.common.manager.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class DataCacheManager {
    public Map<String, Object> cache;

    @PostConstruct
    public void initialize () {
        cache = new HashMap<>();
    }

    @Cacheable(value = "cache", key = "#key")
    public Object find(String key) {

        return cache.get(key);
    }

    @CacheEvict(value = "cache", key = "#key")
    public Object update(String key, Object o) {

        return cache.put(key, o);
    }

    @CacheEvict(value = "cache", key = "#key")
    public void delete(String key) {

        cache.remove(key);
    }

}
