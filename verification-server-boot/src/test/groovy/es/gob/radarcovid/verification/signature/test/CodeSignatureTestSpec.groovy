/**
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.signature.test

import es.gob.radarcovid.common.security.KeyVault
import es.gob.radarcovid.verification.config.SecurityConfiguration
import es.gob.radarcovid.verification.signature.CodeSignature
import es.gob.radarcovid.verification.signature.impl.CodeSignatureImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer
import spock.lang.Shared
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.security.Signature

@SpringBootTest
@ActiveProfiles('test')
class CodeSignatureTestSpec extends Specification {

    @Shared
    PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer('postgres:12-alpine')
            .withDatabaseName('RADARCOVID')
            .withUsername('radarcovid')
            .withPassword('radarcovid')

    @Autowired
    private CodeSignature codeSignature

    @Autowired
    private KeyVault keyVault

    def getSignature(List<String> codes, String keyPairName) {
        StringBuilder stringBuilder = new StringBuilder()
        codes.stream().forEach(stringBuilder::append)

        Signature signature = Signature.getInstance(CodeSignatureImpl.ALGORITHM)
        signature.initVerify(keyVault.get(keyPairName).getPublic())
        signature.update(stringBuilder.toString().getBytes(StandardCharsets.UTF_8))

        return signature
    }

    def 'sign no dummy'(List<String> codes, String result) {
        given:
        String sign = codeSignature.sign(false, codes)
        Signature signature = getSignature(codes, SecurityConfiguration.PAIR_KEY_RADAR)

        when:
        def validated = signature.verify(Base64.getDecoder().decode(sign.getBytes(StandardCharsets.UTF_8)))

        then:
        validated

        where:
        codes                         | result
        Arrays.asList('123456789012') | 'MIGIAkIB7/IoWrSAK023tFw6+Vety7r78ROlX3oAbxeMJs/4F3Q8TJhmB8Qx/GPZ0ENJ3n7EnzsKOjbGymCuahMXgx3h8HwCQgEICrnbRfOpuFRgMC/LmF/jhEo6vh9B21BNYt90R4FKbDco1kHk+SVpHrmEVMud+FJDpOLjCMQ39Z0qqh9rrJv8Iw=='
    }
}
