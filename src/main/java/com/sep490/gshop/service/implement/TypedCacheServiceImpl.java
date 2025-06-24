package com.sep490.gshop.service.implement;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.sep490.gshop.common.enums.CacheType;
import com.sep490.gshop.service.TypedCacheService;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class TypedCacheServiceImpl<K, V> implements TypedCacheService<K, V> {

    private final ConcurrentHashMap<CacheType, Cache<K, V>> cacheMap = new ConcurrentHashMap<>();

    private Cache<K, V> getCache(CacheType type) {
        return cacheMap.computeIfAbsent(type, t ->
                Caffeine.newBuilder()
                        .expireAfterWrite(t.getTtlMinutes(), TimeUnit.MINUTES)
                        .maximumSize(10000)
                        .build()
        );
    }

    @Override
    public void put(CacheType type, K key, V value) {
        getCache(type).put(key, value);
    }

    @Override
    public V get(CacheType type, K key) {
        return getCache(type).getIfPresent(key);
    }

    @Override
    public void remove(CacheType type, K key) {
        getCache(type).invalidate(key);
    }

    @Override
    public boolean contains(CacheType type, K key) {
        return get(type, key) != null;
    }
}
