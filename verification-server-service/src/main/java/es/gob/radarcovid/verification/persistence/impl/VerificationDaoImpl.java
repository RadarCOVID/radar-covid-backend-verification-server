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
import es.gob.radarcovid.verification.persistence.entity.VerificationEntity;
import es.gob.radarcovid.verification.persistence.repository.VerificationRepository;
import es.gob.radarcovid.verification.util.HashingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class VerificationDaoImpl implements VerificationDao {

    private final VerificationRepository repository;
    private final HashingService hashingService;

    public VerificationDaoImpl(
            VerificationRepository repository,
            HashingService hashingService) {
        this.repository = repository;
        this.hashingService = hashingService;
    }

    @Override
    public boolean isValidCode(String code) {
        return repository.findByCodeHashAndCodeRedeemedIsFalse(hashingService.hash(code))
                .filter(t -> t.codeCanBeRedeemed(LocalDateTime.now(ZoneOffset.UTC))).isPresent();
    }

    @Override
    public boolean redeemCode(String code, String tan, Date validUntil) {
        String tanHash = hashingService.hash(tan);
        log.debug("Entering in redeemCode(code = {}, tan = {}, validUntil = {}): tanHash = {}", code, tan, validUntil, tanHash);
        return repository.findByCodeHashAndCodeRedeemedIsFalse(hashingService.hash(code))
                .filter(t -> t.codeCanBeRedeemed(LocalDateTime.now(ZoneOffset.UTC)))
                .map(t -> {
                    Date now = new Date();
                    t.setCodeRedeemedAt(now);
                    t.setTanHash(tanHash);
                    t.setTanValidFrom(now);
                    t.setTanValidUntil(validUntil);
                    repository.save(t);
                    return true;
                }).map(t -> true).orElseGet(() -> false);
    }

    @Override
    public boolean redeemTan(String tan) {
        log.debug("Entering in redeemTan(tan = {})", tan);
        LocalDateTime localDateTimeNow = LocalDateTime.now(ZoneOffset.UTC);
        return repository.findByTanHashAndTanRedeemedIsFalse(hashingService.hash(tan))
                .filter(t -> t.codeCanBeRedeemed(localDateTimeNow) && t.tanCanBeRedeemed(localDateTimeNow))
                .map(t -> {
                    Date now = new Date();
                    t.setCodeRedeemed(true);
                    t.setCodeRedeemedAt(now);
                    t.setTanRedeemed(true);
                    t.setTanRedeemedAt(now);
                    repository.save(t);
                    return true;
                }).map(t -> true).orElseGet(() -> false);
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
