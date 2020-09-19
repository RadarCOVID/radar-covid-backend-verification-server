/**
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.business.test

import com.auth0.jwt.JWT
import es.gob.radarcovid.verification.api.CodeDto
import es.gob.radarcovid.verification.business.VerificationService
import es.gob.radarcovid.verification.persistence.repository.VerificationRepository
import es.gob.radarcovid.verification.util.HashingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles("test")
class VerificationServiceTestSpec extends Specification {

    @Autowired
    VerificationService service

    @Autowired
    HashingService hashingService

    @Autowired
    VerificationRepository repository

    def "get codes"(boolean radarCovid, String ccaa, int number) {
        when:
        def codes = service.getCodes(radarCovid, ccaa, number)
        def hash = hashingService.hash(codes.codes.first())
        def entity = repository.findByCodeHashAndCodeRedeemedIsFalse(hash)

        then:
        codes.codes.size() == number
        entity.get().ccaa == ccaa
        entity.get().codeHash == hash
        entity.get().ccaaCreation
        !entity.get().codeRedeemed
        !entity.get().tanRedeemed
        entity.get().tanHash == null

        where:
        radarCovid | ccaa | number
        false      | "01" | 1
    }

    def "redeem code"(boolean radarCovid, String ccaa, int number) {
        given:
        def codes = service.getCodes(radarCovid, ccaa, number)
        def codeDto = new CodeDto()
        codeDto.code = codes.codes.first()

        when:
        def jwt = service.redeemCode(codeDto)
        def decodedJWT = JWT.decode(jwt.get())
        def codeHash = hashingService.hash(codes.codes.first())
        def entityCode = repository.findByCodeHashAndCodeRedeemedIsFalse(codeHash)
        def tan = decodedJWT.getClaim("tan").asString()
        def tanHash = hashingService.hash(tan)
        def entityTan = repository.findByTanHashAndTanRedeemedIsFalse(tanHash)

        then:
        decodedJWT.subject == codes.codes.first()
        decodedJWT.getClaim("scope").asString() == "exposed"
        entityCode.get().ccaa == ccaa
        entityCode.get().codeHash == codeHash
        entityCode.get().ccaaCreation
        !entityCode.get().codeRedeemed
        !entityCode.get().tanRedeemed
        entityCode.get().tanHash == tanHash
        entityCode.get().id == entityTan.get().id

        where:
        radarCovid | ccaa | number
        false      | "01" | 1
    }

    def "redeem TAN"(boolean radarCovid, String ccaa, int number) {
        given:
        def codes = service.getCodes(radarCovid, ccaa, number)
        def codeDto = new CodeDto()
        codeDto.code = codes.codes.first()

        when:
        def jwt = service.redeemCode(codeDto)
        def decodedJWT = JWT.decode(jwt.get())

        def codeHash = hashingService.hash(codes.codes.first())
        def entityCode = repository.findByCodeHashAndCodeRedeemedIsFalse(codeHash)

        def tan = decodedJWT.getClaim("tan").asString()
        def tanHash = hashingService.hash(tan)

        def redeemed = service.redeemTan(tan)
        def entityTan = repository.findById(entityCode.get().id)

        then:
        decodedJWT.subject == codes.codes.first()
        decodedJWT.getClaim("scope").asString() == "exposed"
        entityCode.get().ccaa == ccaa
        entityCode.get().codeHash == codeHash
        entityCode.get().ccaaCreation
        !entityCode.get().codeRedeemed
        !entityCode.get().tanRedeemed
        entityCode.get().tanHash == tanHash
        redeemed
        entityTan.get().codeRedeemed
        entityTan.get().tanRedeemed
        entityTan.get().tanHash == tanHash

        where:
        radarCovid | ccaa | number
        false      | "01" | 1
    }

}
