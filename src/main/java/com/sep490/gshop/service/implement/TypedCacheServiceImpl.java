package com.sep490.gshop.service.implement;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.sep490.gshop.common.enums.CacheType;
import com.sep490.gshop.common.shared.CacheData;
import com.sep490.gshop.service.TypedCacheService;
import com.sep490.gshop.utils.DateTimeUtil;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class TypedCacheServiceImpl<K, V> implements TypedCacheService<K, V> {

    private final ConcurrentHashMap<CacheType, Cache<K, CacheData<V>>> cacheMap = new ConcurrentHashMap<>();

    private Cache<K, CacheData<V>> getCache(CacheType type) {
        return cacheMap.computeIfAbsent(type, t ->
                Caffeine.newBuilder()
                        .expireAfterWrite(t.getTtlMinutes(), TimeUnit.MINUTES)
                        .maximumSize(10000)
                        .build()
        );
    }

    @Override
    public void put(CacheType type, K key, V value) {
        CacheData<V> cacheData = new CacheData<>(value, DateTimeUtil.getCurrentEpochMilli() + type.getTtlMinutes() * 60 * 1000);
        getCache(type).put(key, cacheData);
    }

    @Override
    public V get(CacheType type, K key) {
        CacheData<V> cacheData = getCache(type).getIfPresent(key);
        if (cacheData == null || cacheData.getExpiredTime() < System.currentTimeMillis()) {
            return null;
        }
        return Objects.requireNonNull(getCache(type).getIfPresent(key)).getData();
    }

    @Override
    public void remove(CacheType type, K key) {
        getCache(type).invalidate(key);
    }

    @Override
    public boolean contains(CacheType type, K key) {
        return get(type, key) != null;
    }

    @Override
    public long getTimeRemaining(CacheType type, K key) {
        CacheData<V> cacheData = getCache(type).getIfPresent(key);
        if (cacheData == null) {
            return 0;
        }
        long remainingTime = (cacheData.getExpiredTime() - DateTimeUtil.getCurrentEpochMilli())/1000;
        return remainingTime > 0 ? remainingTime : 0;
    }
}
