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
import es.gob.radarcovid.common.exception.RadarCovidServerException;
import es.gob.radarcovid.verification.api.CodeDto;
import es.gob.radarcovid.verification.business.VerificationService;
import es.gob.radarcovid.verification.persistence.VerificationDao;
import es.gob.radarcovid.verification.security.JwtGenerator;
import es.gob.radarcovid.verification.util.CheckSumUtil;
import es.gob.radarcovid.verification.util.DateInfection;
import es.gob.radarcovid.verification.util.GenerateRandom;
import es.gob.radarcovid.verification.validation.impl.CodeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationServiceImpl implements VerificationService {

    private final VerificationDao dao;
    private final GenerateRandom generateRandom;
    private final JwtGenerator jwtGenerator;
    private final DateInfection dateInfection;

    @Value("${application.entities.redeem.tan}")
    private int tanValidUntilMinutes;

    @Value("${application.random.tan.size}")
    private int randomTanSize;

    @Loggable
    @Override
    public Optional<String> redeemCode(CodeDto codeDto, boolean efgsSharing) {
        if (codeDto != null && !StringUtils.isEmpty(codeDto.getCode())) {
            String strTan = generateTan(codeDto.getCode());
            Instant validUntilTime = Instant.now().plus(tanValidUntilMinutes, ChronoUnit.MINUTES);
            Date validUntil = Date.from(validUntilTime);
            if (CodeValidator.FAKE_CODE.equals(codeDto.getCode())) {
                log.debug("Redeem fake code " + CodeValidator.FAKE_CODE);
				return Optional.of(jwtGenerator.generateJwt(true, CodeValidator.FAKE_CODE, strTan,
						dateInfection.getDefaultInfectionDate(), validUntil, false));
            } else if (CheckSumUtil.validateChecksum(codeDto.getCode())) {
                log.debug("TAN:{}", strTan);
				if (dao.redeemCode(codeDto.getCode(), strTan, validUntil)) {
					return Optional.of(jwtGenerator.generateJwt(false, codeDto.getCode(), strTan,
							dateInfection.getInfectionDate(codeDto.getDate()), validUntil, efgsSharing));
				}
            }
        }
        throw new RadarCovidServerException(HttpStatus.BAD_REQUEST, "Code " + codeDto + " is invalid");
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

}
