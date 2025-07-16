package com.sep490.gshop.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.sep490.gshop.common.enums.CacheType;
import com.sep490.gshop.payload.response.ExchangeRateResponse;

public interface TypedCacheService<K, V> {
    void put(CacheType type, K key, V value);
    V get(CacheType type, K key);
    void remove(CacheType type, K key);
    boolean contains(CacheType type, K key);
    long getTimeRemaining(CacheType type, K key);
    //Exchange-rate[phamhminhkhoi]
    Cache<String, ExchangeRateResponse> exchangeRateCache();
}
