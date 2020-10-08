/**
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.controller.test

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import es.gob.radarcovid.common.security.KeyVault
import es.gob.radarcovid.verification.api.CodesResultDto
import es.gob.radarcovid.verification.security.JwtAuthorizationFilter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.charset.StandardCharsets
import java.security.Signature
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles('test')
class GenerationControllerTestSpec extends Specification {

    def PRIVATE_KEY_FILE = 'classpath://generated_private_base64.pem';
    def PUBLIC_KEY_FILE = 'classpath://generated_pub_base64_server.pem';

    def Logger log = LoggerFactory.getLogger(GenerationControllerTestSpec.class)

    def String ALGORITHM_EC = 'EC'
    def String ALGORITHM_SHA512 = 'SHA512withECDSA'
    def String CCAA_ISSUER = 'ISSUER'
    def int TOKEN_MINS_EXPIRES = 15

    @Autowired
    TestRestTemplate testRestTemplate;

    @Unroll
    def 'ask for verification codes with subject [#subject], numCodes [#numCodes] and statusCode [#statusCode]'(String subject, int numCodes, int statusCode) {
        given:
        HttpHeaders httpHeaders = new HttpHeaders()

        httpHeaders.setContentType(MediaType.APPLICATION_JSON)
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON))
        httpHeaders.set(JwtAuthorizationFilter.AUTHORIZATION_HEADER, JwtAuthorizationFilter.AUTHORIZATION_PREFIX + generateToken(subject))

        HttpEntity<?> request = new HttpEntity<>(httpHeaders)

        when: 'request the generation endpoint'
        def result = testRestTemplate.exchange('/generate?n={number}', HttpMethod.GET, request, CodesResultDto.class, numCodes)

        then:
        result.statusCode.value == statusCode
        (result.statusCode.is2xxSuccessful() && result.body.codes.size() == numCodes && validateResponse(result.body.signature, result.body.codes)) || !(result.statusCode.is2xxSuccessful())

        where:
        subject | numCodes | statusCode
        '01'    | 5        | 200         // CCAA 01 is loaded in database
        '02'    | 1        | 403         // CCAA 02 is not loaded in database
    }

    @Unroll
    def 'ask for verification codes with subject [#subject], numCodes [#numCodes] and statusCode [#statusCode] and alg none'(String subject, int numCodes, int statusCode) {
        given:
        HttpHeaders httpHeaders = new HttpHeaders()

        httpHeaders.setContentType(MediaType.APPLICATION_JSON)
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON))
        httpHeaders.set(JwtAuthorizationFilter.AUTHORIZATION_HEADER, JwtAuthorizationFilter.AUTHORIZATION_PREFIX + generateAlgNoneToken(subject))

        HttpEntity<?> request = new HttpEntity<>(httpHeaders)

        when: 'request the generation endpoint'
        def result = testRestTemplate.exchange('/generate?n={number}', HttpMethod.GET, request, CodesResultDto.class, numCodes)

        then:
        result.statusCode.value == statusCode
        (result.statusCode.is2xxSuccessful() && result.body.codes.size() == numCodes && validateResponse(result.body.signature, result.body.codes)) || !(result.statusCode.is2xxSuccessful())

        where:
        subject | numCodes | statusCode
        '01'    | 5        | 403         // CCAA 01 is loaded in database
    }

    def generateToken(String subject) throws Exception {
        String strPrivateKey = KeyVault.loadKey(PRIVATE_KEY_FILE)
        ECPrivateKey privateKey = (ECPrivateKey) KeyVault.loadPrivateKeyFromPem(strPrivateKey, ALGORITHM_EC)

        Algorithm algorithm = Algorithm.ECDSA512(null, privateKey)

        Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS)
        Instant expiresAt = issuedAt.plus(TOKEN_MINS_EXPIRES, ChronoUnit.MINUTES)

        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withSubject(subject)
                .withIssuer(CCAA_ISSUER)
                .withIssuedAt(Date.from(issuedAt))
                .withExpiresAt(Date.from(expiresAt))
                .sign(algorithm)
    }

    def generateAlgNoneToken(String subject) throws Exception {
        String strPrivateKey = KeyVault.loadKey(PRIVATE_KEY_FILE)
        ECPrivateKey privateKey = (ECPrivateKey) KeyVault.loadPrivateKeyFromPem(strPrivateKey, ALGORITHM_EC)

        Algorithm algorithm = Algorithm.ECDSA512(null, privateKey)

        Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS)
        Instant expiresAt = issuedAt.plus(TOKEN_MINS_EXPIRES, ChronoUnit.MINUTES)

        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withSubject(subject)
                .withIssuer(CCAA_ISSUER)
                .withIssuedAt(Date.from(issuedAt))
                .withExpiresAt(Date.from(expiresAt))
                .sign(Algorithm.none())
    }

    def validateResponse(String responseSignature, List<String> codes) throws Exception {
        StringBuilder stringBuilder = new StringBuilder()
        codes.stream().forEach(stringBuilder::append)

        String strPublicKey = KeyVault.loadKey(PUBLIC_KEY_FILE)
        ECPublicKey publicKey = (ECPublicKey) KeyVault.loadPublicKeyFromPem(strPublicKey, ALGORITHM_EC)
        Signature signature = Signature.getInstance(ALGORITHM_SHA512)
        signature.initVerify(publicKey)
        signature.update(stringBuilder.toString().getBytes(StandardCharsets.UTF_8))

        return signature.verify(Base64.getDecoder().decode(responseSignature.getBytes(StandardCharsets.UTF_8)))
    }

}