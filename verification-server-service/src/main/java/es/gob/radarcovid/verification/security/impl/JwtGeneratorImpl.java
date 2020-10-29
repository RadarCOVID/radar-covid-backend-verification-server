/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.security.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import es.gob.radarcovid.common.security.KeyVault;
import es.gob.radarcovid.verification.config.SecurityConfiguration;
import es.gob.radarcovid.verification.security.JwtGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtGeneratorImpl implements JwtGenerator {

    private static final String CLAIM_TAN = "tan";
    private static final String CLAIM_FAKE = "fake";
    private static final String CLAIM_ONSET = "onset";
    private static final String CLAIM_SCOPE_NAME = "scope";
    private static final String CLAIM_EFGS_SHARING = "efgs";

    private static final String CLAIM_SCOPE_VALUE = "exposed";
    private static final int CLAIM_FAKE_VALUE_TRUE = 1;
    private static final int CLAIM_FAKE_VALUE_FALSE = 0;

    private static final ThreadLocal<DateFormat> DATE_TIME_FORMATTER = ThreadLocal.<DateFormat>withInitial(
            () -> new SimpleDateFormat("yyyy-MM-dd")
    );

    @Value("${application.jwt.issuer}")
    private String jwtIssuer;

    private final KeyVault keyVault;

    @Override
    public String generateJwt(boolean isFake, String code, String tan, Date exposedDate, Date validUntil,
                              boolean efgsSharing) {

        KeyPair keyPair = keyVault.get(SecurityConfiguration.PAIR_KEY_RADAR);
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();

        Algorithm algorithm = Algorithm.ECDSA512(publicKey, privateKey);
        String jwtId = UUID.randomUUID().toString();
        Date issuedAt = new Date();

        String token = JWT.create()
                .withJWTId(jwtId)
                .withSubject(code)
                .withIssuer(jwtIssuer)
                .withIssuedAt(issuedAt)
                .withExpiresAt(validUntil)
                .withClaim(CLAIM_TAN, tan)
                .withClaim(CLAIM_ONSET, DATE_TIME_FORMATTER.get().format(exposedDate))
                .withClaim(CLAIM_SCOPE_NAME, CLAIM_SCOPE_VALUE)
                .withClaim(CLAIM_FAKE, isFake ? CLAIM_FAKE_VALUE_TRUE : CLAIM_FAKE_VALUE_FALSE)
                .withClaim(CLAIM_EFGS_SHARING, efgsSharing)
                .sign(algorithm);
        if (!isFake) {
            log.info("Token {} - code {} - issuedAt {} - expiresAt {}", jwtId, code, issuedAt, validUntil);
        }
        return token;
    }

}
