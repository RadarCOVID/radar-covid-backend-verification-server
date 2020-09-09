/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.config;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import es.gob.radarcovid.verification.etc.CacheConstants;
import es.gob.radarcovid.verification.handler.CustomizedCacheErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnProperty(name = "application.cache.enabled", havingValue = "true")
@EnableCaching
@Slf4j
public class CacheConfiguration extends CachingConfigurerSupport implements CachingConfigurer {

    @Override
    public CacheErrorHandler errorHandler() {
        return new CustomizedCacheErrorHandler();
    }

    @Value("${application.cache.max-size:200}")
    private int cacheMaxSize;

    @Value("${application.cache.time-to-live:5}")
    private int cacheTimeToLiveMinutes;

    @Value("${application.cache.max-idle:20}")
    private int cacheMaxIdleSeconds;

    @Bean
    @DependsOn("noOpCacheManager")
    @Primary
    public CacheManager kpiCacheManager(@Qualifier("noOpCacheManager") CacheManager noOpCacheManager) {
        CompositeCacheManager compositeCacheManager = new CompositeCacheManager();
        compositeCacheManager.setFallbackToNoOpCache(true);
        List<CacheManager> cacheManagerList = new ArrayList<>();
        cacheManagerList.add(remoteCacheManager(noOpCacheManager));
        cacheManagerList.add(noOpCacheManager);
        compositeCacheManager.setCacheManagers(cacheManagerList);
        return compositeCacheManager;
    }

    @Bean("noOpCacheManager")
    public CacheManager noOpCacheManager() {
        return new NoOpCacheManager();
    }

    private HazelcastInstance hazelcastInstanceLocal() {
        Config config = new Config().setInstanceName("hazelcast-instance");
        config.addMapConfig(mapConfig(CacheConstants.CACHE_CCAA_ID));
        config.addMapConfig(mapConfig(CacheConstants.CACHE_CCAA_LIST));
        NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.setPort(5701).setPortCount(20);
        networkConfig.setPortAutoIncrement(true);
        JoinConfig joinConfig = networkConfig.getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getTcpIpConfig().addMember("localhost").setEnabled(true);
        return Hazelcast.newHazelcastInstance(config);
    }

    private CacheManager remoteCacheManager(CacheManager noOpCacheManager) {
        CacheManager cacheManager;
        try {
            HazelcastInstance hazelcastInstance = hazelcastInstanceLocal();
            log.debug("Hazelcast Instance Local");
            cacheManager = new HazelcastCacheManager(hazelcastInstance);
        } catch (Exception ex) {
            cacheManager = noOpCacheManager;
            log.debug("Hazelcast disabled");
        }

        return cacheManager;
    }

    private MapConfig mapConfig(String cacheName) {
        MapConfig mapConfig = new MapConfig(cacheName);
        mapConfig.setMaxSizeConfig(new MaxSizeConfig(cacheMaxSize, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE));
        mapConfig.setTimeToLiveSeconds(cacheTimeToLiveMinutes * 60);
        mapConfig.setMaxIdleSeconds(cacheMaxIdleSeconds);
        return mapConfig;
    }

}
