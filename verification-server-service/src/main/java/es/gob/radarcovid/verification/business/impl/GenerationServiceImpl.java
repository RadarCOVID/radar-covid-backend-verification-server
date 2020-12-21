/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.business.impl;

import es.gob.radarcovid.common.annotation.Loggable;
import es.gob.radarcovid.verification.api.CodesResultDto;
import es.gob.radarcovid.verification.business.GenerationService;
import es.gob.radarcovid.verification.domain.CCAADto;
import es.gob.radarcovid.verification.persistence.CCAADao;
import es.gob.radarcovid.verification.persistence.VerificationDao;
import es.gob.radarcovid.verification.signature.CodeSignature;
import es.gob.radarcovid.verification.util.GenerateRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenerationServiceImpl implements GenerationService {

    private final VerificationDao verificationDao;
    private final CCAADao ccaaDao;
    private final GenerateRandom generateRandom;
    private final CodeSignature codeSignature;

    @Value("${application.dummy.enabled:false}")
    private boolean dummyEnabled;

    @Value("${application.dummy.ccaa:DUMMY}")
    private String dummyCCAA;

    @Value("${application.entities.redeem.code}")
    private int codeValidUntilDays;

    @Value("${application.random.code.size:12}")
    private int randomCodeSize;

    @Loggable
    @Override
    public CodesResultDto getCodes(boolean radarCovid, String ccaa, Integer number) {
        Instant validUntilTime = Instant.now().plus(codeValidUntilDays, ChronoUnit.DAYS);
        Date validUntil = Date.from(validUntilTime);

        List<String> codes = new ArrayList<>();
        IntStream.range(0, number).forEach(i -> {
            codes.add(generateRandom.generateRandomDigits(randomCodeSize));
        });
        verificationDao.saveCodes(radarCovid, ccaa, codes, validUntil);

        Optional<CCAADto> ccaaDto = ccaaDao.findById(ccaa);
        boolean isDummy = dummyEnabled && ccaaDto.isPresent() && dummyCCAA.equals(ccaaDto.get().getIssuer());

        CodesResultDto result = new CodesResultDto();
        result.setCodes(codes);
        result.setValidUntil(validUntil);

        try {
            if (isDummy && log.isDebugEnabled()) {
                log.debug("Firmando con clave privada dummy para CCAA {}", ccaa);
            }
            result.setSignature(codeSignature.sign(isDummy, codes));
        } catch (Exception e) {
            log.error("Error firmando códigos: {}", e.getMessage(), e);
        }
        return result;
    }

}
