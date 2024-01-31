package com.ite5year.services;

public interface CacheService {
    void evictSingleCacheValue(String cacheName, String cacheKey);
    void evictAllCacheValues(String cacheName);
}
