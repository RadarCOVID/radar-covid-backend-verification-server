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
import es.gob.radarcovid.common.exception.VerificationServerException;
import es.gob.radarcovid.verification.api.CodeDto;
import es.gob.radarcovid.verification.api.CodesResultDto;
import es.gob.radarcovid.verification.business.VerificationService;
import es.gob.radarcovid.verification.domain.CCAADto;
import es.gob.radarcovid.verification.persistence.CCAADao;
import es.gob.radarcovid.verification.persistence.VerificationDao;
import es.gob.radarcovid.verification.security.JwtGenerator;
import es.gob.radarcovid.verification.signature.CodeSignature;
import es.gob.radarcovid.verification.util.CheckSumUtil;
import es.gob.radarcovid.verification.util.GenerateRandom;
import es.gob.radarcovid.verification.validation.impl.CodeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationServiceImpl implements VerificationService {

    private final VerificationDao dao;
    private final CCAADao ccaaDao;
    private final GenerateRandom generateRandom;
    private final JwtGenerator jwtGenerator;
    private final CodeSignature codeSignature;

    @Value("${application.dummy.enabled:false}")
    private boolean dummyEnabled;

    @Value("${application.dummy.ccaa:DUMMY}")
    private String dummyCCAA;

    @Value("${application.entities.redeem.code}")
    private int codeValidUntilDays;

    @Value("${application.entities.redeem.tan}")
    private int tanValidUntilMinutes;

    @Value("${application.random.tan.size}")
    private int randomTanSize;

    @Value("${application.random.code.size:12}")
    private int randomCodeSize;

    @Value("${application.jwt.onset.default}")
    private int onsetDefaultDays;

    @Value("${application.jwt.onset.app}")
    private int onsetAppDays;

    @Loggable
    @Override
    public Optional<String> redeemCode(CodeDto codeDto) {
        if (codeDto != null && !StringUtils.isEmpty(codeDto.getCode())) {
            String strTan = generateTan(codeDto.getCode());
            Instant validUntilTime = Instant.now().plus(tanValidUntilMinutes, ChronoUnit.MINUTES);
            Date validUntil = Date.from(validUntilTime);
            if (CodeValidator.FAKE_CODE.equals(codeDto.getCode())) {
                LocalDate exposedLocalDate = LocalDate.now().minusDays(onsetDefaultDays);
                Date exposedDate = Date.from(exposedLocalDate.atStartOfDay(ZoneOffset.UTC).toInstant());
                log.debug("Redeem fake code " + CodeValidator.FAKE_CODE);
                return Optional.of(jwtGenerator.generateJwt(true, CodeValidator.FAKE_CODE, strTan, exposedDate, validUntil));
            } else if (CheckSumUtil.validateChecksum(codeDto.getCode())) {
                log.debug("TAN:{}", strTan);
                if (dao.redeemCode(codeDto.getCode(), strTan, validUntil)) {

                    LocalDate exposedLocalDate = codeDto.getDate() == null ?
                            LocalDate.now().minusDays(onsetDefaultDays) :
                            LocalDate.ofInstant(codeDto.getDate().toInstant(), ZoneOffset.UTC).minusDays(onsetAppDays);

                    Date exposedDate = Date.from(exposedLocalDate.atStartOfDay(ZoneOffset.UTC).toInstant());

                    return Optional.of(jwtGenerator.generateJwt(false, codeDto.getCode(), strTan, exposedDate, validUntil));
                }
            }
        }
        throw new VerificationServerException(HttpStatus.BAD_REQUEST, "Code " + codeDto + " is invalid");
    }

    @Loggable
    @Override
    public boolean redeemTan(String tan) {
        return dao.redeemTan(tan);
    }

    private String generateTan(String code) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(code.substring(2,8));
        stringBuilder.append(generateRandom.getAlphaNumericString(randomTanSize - 8));
        stringBuilder.append(StringUtils.leftPad(Long.toString(System.currentTimeMillis() % 100), 2, '0'));
        return stringBuilder.toString();
    }

    @Loggable
    @Override
    public CodesResultDto getCodes(boolean radarCovid, String ccaa, Integer number) {
        List<String> codes = new ArrayList<>();
        Instant validUntilTime = Instant.now().plus(codeValidUntilDays, ChronoUnit.DAYS);
        Date validUntil = Date.from(validUntilTime);
        IntStream.range(0, number).forEach(i -> {
            codes.add(generateRandom.generateRandomDigits(randomCodeSize));
        });
        dao.saveCodes(radarCovid, ccaa, codes, validUntil);

        Optional<CCAADto> ccaaDto = ccaaDao.findById(ccaa);
        boolean dummy = dummyEnabled && ccaaDto.isPresent() && dummyCCAA.equals(ccaaDto.get().getIssuer());

        CodesResultDto result = new CodesResultDto();
        result.setCodes(codes);
        result.setValidUntil(validUntil);

        try {
            if (dummy && log.isDebugEnabled()) {
                log.debug("Firmando con clave privada dummy para CCAA {}", ccaa);
            }
            result.setSignature(codeSignature.sign(dummy, codes));
        } catch (Exception e) {
            log.error("Error firmando códigos: {}", e.getMessage(), e);
        }
        return result;
    }
}
