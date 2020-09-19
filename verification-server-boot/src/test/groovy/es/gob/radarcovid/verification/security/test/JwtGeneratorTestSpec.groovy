/**
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.security.test

import com.auth0.jwt.JWT
import es.gob.radarcovid.verification.security.JwtGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import java.text.SimpleDateFormat

@SpringBootTest
@ActiveProfiles("test")
class JwtGeneratorTestSpec extends Specification {

    @Autowired
    JwtGenerator jwtGenerator

    def "generate JWT"(String code, String tan, String exposedDateStr, String validUntilStr) {
        given:
        def dateFormat = new SimpleDateFormat("yyyy-MM-dd")
        def exposedDate = dateFormat.parse(exposedDateStr)
        def validUntil  = dateFormat.parse(validUntilStr)
        String jwt = jwtGenerator.generateJwt(code, tan, exposedDate, validUntil)

        when:
        def decodedJWT = JWT.decode(jwt)

        then:
        decodedJWT.subject == code
        decodedJWT.getClaim("tan").asString() == tan
        decodedJWT.getClaim("onset").asString() == exposedDateStr
        decodedJWT.getClaim("scope").asString() == "exposed"

        where:
        code           | tan    | exposedDateStr | validUntilStr
        "123456789012" | "XXXX" | "2020-08-30"   | "2020-09-14"
    }

}
