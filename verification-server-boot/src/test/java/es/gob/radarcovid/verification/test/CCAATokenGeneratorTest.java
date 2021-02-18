/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import es.gob.radarcovid.common.security.KeyVault;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class CCAATokenGeneratorTest {

    private static final String PRIVATE_KEY_FILE = "classpath://generated_private_base64.pem";
    private static final String PUBLIC_KEY_FILE = "classpath://generated_pub_base64.pem";

    private static final String ALGORITHM = "EC";
    private static final String CCAA_SUBJECT = "00"; // Autonomous Community code from https://www.ine.es/daco/daco42/codmun/cod_ccaa.htm
    private static final String CCAA_ISSUER = "sedia-02";
    private static final int TOKEN_MINS_EXPIRES = 15;

    @Test
    public void testCreateToken() throws Exception {
        String token = generateToken();
        log.info("JWT = {}", token);
        boolean valid = validateToken(token);
        log.info("Token is {}", valid ? "valid" : "invalid");
        Assertions.assertTrue(valid);
    }

    private String generateToken() throws Exception {
        String strPrivateKey = KeyVault.loadKey(PRIVATE_KEY_FILE);
        ECPrivateKey privateKey = (ECPrivateKey) KeyVault.loadPrivateKeyFromPem(strPrivateKey, ALGORITHM);

        Algorithm algorithm = Algorithm.ECDSA512(null, privateKey);

        Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant expiresAt = issuedAt.plus(TOKEN_MINS_EXPIRES, ChronoUnit.MINUTES);

        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withSubject(CCAA_SUBJECT)
                .withIssuer(CCAA_ISSUER)
                .withIssuedAt(Date.from(issuedAt))
                .withExpiresAt(Date.from(expiresAt))
                .sign(algorithm);
    }

    private boolean validateToken(String token) {
        boolean result = false;
        try {
            String strPublicKey = KeyVault.loadKey(PUBLIC_KEY_FILE);
            ECPublicKey publicKey = (ECPublicKey) KeyVault.loadPublicKeyFromPem(strPublicKey, ALGORITHM);

            Algorithm algorithm = Algorithm.ECDSA512(publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(CCAA_ISSUER).build();
            DecodedJWT jwt = verifier.verify(token);
            result = CCAA_SUBJECT.equals(jwt.getSubject());
        } catch (Exception e) {
            log.error("Exception validating token: {}", e.getMessage(), e);
        }

        return result;
    }

}
