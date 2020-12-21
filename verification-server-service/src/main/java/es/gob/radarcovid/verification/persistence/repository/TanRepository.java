/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.persistence.repository;

import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import es.gob.radarcovid.verification.etc.CacheConstants;
import es.gob.radarcovid.verification.persistence.entity.TanEntity;

@NoRepositoryBean
public interface TanRepository extends Repository<TanEntity, String> {
	
    @CachePut(cacheNames = CacheConstants.CACHE_TAN_HASH, key = "#tanEntity.tanHash")
    default TanEntity save(TanEntity tanEntity) {
    	return tanEntity;
    }
    
    @Cacheable(cacheNames = CacheConstants.CACHE_TAN_HASH, key = "#tanHash", unless = "#result == null")
    default Optional<TanEntity> findByTanHash(String tanHash) {
    	return Optional.empty();
    }
    
    @CacheEvict(cacheNames = CacheConstants.CACHE_TAN_HASH, key = "#tanHash")
    default void deleteByTanHash(String tanHash) {
    	return;
    }

}
