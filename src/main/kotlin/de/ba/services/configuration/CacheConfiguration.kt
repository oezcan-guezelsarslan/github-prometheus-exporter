package de.ba.services.configuration

import org.slf4j.LoggerFactory
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableCaching
class CacheConfiguration(private val cacheManager: CacheManager) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = $$"${cache.configuration.clear.cron}")
    fun clearCache() {
        logger.info("Perform clear cache")
        cacheManager.cacheNames
            .forEach { cacheName -> cacheManager.getCache(cacheName)?.clear() }
    }
}