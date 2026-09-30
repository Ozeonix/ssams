package com.artms.shared.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Enterprise Redis L2 Caching Configuration for High-Concurrency (50k+ Users).
 * 
 * Provides:
 * 1. Distributed Redis L2 caching with granular TTLs per business entity
 * 2. JSON serialization for clean Redis inspection
 * 3. Graceful fallback CacheErrorHandler (prevents 500 errors if Redis is restarting)
 */
@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    // Well-known Cache Names
    public static final String CACHE_TENANT_CONFIG   = "tenant:config";
    public static final String CACHE_ACADEMIC_TERMS  = "academic:terms";
    public static final String CACHE_FEE_STRUCTURE   = "fee:structures";
    public static final String CACHE_EXAM_RESULTS    = "exam:results";
    public static final String CACHE_STUDENT_PROFILE = "student:profiles";

    @Bean
    public RedisCacheConfiguration defaultCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> builder
                .withCacheConfiguration(CACHE_TENANT_CONFIG,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)))
                .withCacheConfiguration(CACHE_ACADEMIC_TERMS,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration(CACHE_FEE_STRUCTURE,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(15)))
                .withCacheConfiguration(CACHE_EXAM_RESULTS,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration(CACHE_STUDENT_PROFILE,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)));
    }

    /**
     * Resilient error handler: If Redis times out or reconnects during peak traffic bursts,
     * the application logs a warning and degrades gracefully by serving from the database
     * rather than throwing exceptions to end users.
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                log.warn("Redis Cache GET failed for key [{}] in cache [{}]. Degrading to DB query: {}", key, cache.getName(), exception.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException exception, org.springframework.cache.Cache cache, Object key, Object value) {
                log.warn("Redis Cache PUT failed for key [{}] in cache [{}]: {}", key, cache.getName(), exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                log.warn("Redis Cache EVICT failed for key [{}] in cache [{}]: {}", key, cache.getName(), exception.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, org.springframework.cache.Cache cache) {
                log.warn("Redis Cache CLEAR failed for cache [{}]: {}", cache.getName(), exception.getMessage());
            }
        };
    }
}
