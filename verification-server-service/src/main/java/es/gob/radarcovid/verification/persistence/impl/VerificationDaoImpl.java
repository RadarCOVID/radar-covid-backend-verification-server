/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.persistence.impl;

import es.gob.radarcovid.verification.persistence.VerificationDao;
import es.gob.radarcovid.verification.persistence.entity.TanEntity;
import es.gob.radarcovid.verification.persistence.entity.VerificationEntity;
import es.gob.radarcovid.verification.persistence.repository.TanRepository;
import es.gob.radarcovid.verification.persistence.repository.VerificationRepository;
import es.gob.radarcovid.verification.util.HashingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationDaoImpl implements VerificationDao {

    private final VerificationRepository repository;
    private final TanRepository tanRepository;
    private final HashingService hashingService;

    @Override
	public boolean redeemCode(String code, String tan, Date validUntil) {
		String tanHash = hashingService.hash(tan);
		String codeHash = hashingService.hash(code);
		log.debug("Entering in redeemCode(code = {}, tan = {}, validUntil = {}): tanHash = {}", code, tan, validUntil,
				tanHash);
		
		boolean result = repository.findByCodeHashAndCodeRedeemedIsFalse(codeHash)
			.filter(v -> v.codeCanBeRedeemed(LocalDateTime.now(ZoneOffset.UTC)))
			.map(v -> {
				Date now = new Date();
				v.setCodeRedeemedAt(now);
				TanEntity tanEntity = TanEntity.builder().tanHash(tanHash).tanValidFrom(now)
						.tanValidUntil(validUntil).codeHash(codeHash).build();
				repository.save(v);
				tanRepository.save(tanEntity);
				return true;
			}).orElseGet(() -> false);
		log.debug("Leaving redeemCode() with: {}", result);
		return result;
	}

    @Override
    public boolean redeemTan(String tan) {
        log.debug("Entering in redeemTan(tan = {})", tan);
        String tanHash = hashingService.hash(tan);
        LocalDateTime localDateTimeNow = LocalDateTime.now(ZoneOffset.UTC);
        
        boolean result = tanRepository.findByTanHash(tanHash)
			.filter(t -> t.tanCanBeRedeemed(localDateTimeNow))
			.flatMap(t -> repository.findByCodeHashAndCodeRedeemedIsFalse(t.getCodeHash()))
			.filter(v -> v.codeCanBeRedeemed(localDateTimeNow))
            .map(v -> {
            	Date now = new Date();
                v.setCodeRedeemed(true);
                v.setCodeRedeemedAt(now);
                repository.save(v);
                tanRepository.deleteByTanHash(tanHash);
                return true;
            }).orElseGet(() -> false);
        log.debug("Leaving redeemTan() with: {}", result);
        return result;
    }

    @Override
    public boolean saveCodes(boolean radarCovid, String ccaa, List<String> codes, Date validUntil) {
        Date now = new Date();
        List<VerificationEntity> entities = new ArrayList<>();
        codes.stream().forEach(c -> {
            VerificationEntity entity = new VerificationEntity();
            entity.setCreatedAt(now);
            entity.setCcaa(ccaa);
            entity.setCcaaCreation(!radarCovid);
            entity.setCodeValidFrom(now);
            entity.setCodeValidUntil(validUntil);
            entity.setCodeHash(hashingService.hash(c));
            entities.add(entity);
        });
        repository.saveAll(entities);
        return true;
    }
    
}
