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
import es.gob.radarcovid.verification.business.GenerationService
import es.gob.radarcovid.verification.business.VerificationService
import es.gob.radarcovid.verification.persistence.repository.TanRepository
import es.gob.radarcovid.verification.persistence.repository.VerificationRepository
import es.gob.radarcovid.verification.util.HashingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification
import spock.lang.Unroll

@SpringBootTest
@ActiveProfiles('test')
class VerificationServiceTestSpec extends Specification {

    @Autowired
    GenerationService generationService

    @Autowired
    VerificationService service

    @Autowired
    HashingService hashingService

    @Autowired
    VerificationRepository repository
	
	@Autowired
	TanRepository tanRepository

    @Unroll
    def 'redeem code with radarCovid [#radarCovid], ccaa [#ccaa] and number [#number]'(boolean radarCovid, String ccaa, int number, boolean efgsSharing) {
        given:
        def codes = generationService.getCodes(radarCovid, ccaa, number)
        def codeDto = new CodeDto()
        codeDto.code = codes.codes.first()

        when:
        def jwt = service.redeemCode(codeDto, efgsSharing)
        def decodedJWT = JWT.decode(jwt.get())
        def codeHash = hashingService.hash(codes.codes.first())
        def entityCode = repository.findByCodeHashAndCodeRedeemedIsFalse(codeHash)
        def tan = decodedJWT.getClaim('tan').asString()
        def tanHash = hashingService.hash(tan)
		def entityTan = tanRepository.findByTanHash(tanHash)

        then:
        decodedJWT.subject == codes.codes.first()
        decodedJWT.getClaim('scope').asString() == 'exposed'
        decodedJWT.getClaim('efgs').asBoolean() == efgsSharing
        entityCode.get()
        entityCode.get().ccaa == ccaa
        entityCode.get().codeHash == codeHash
        entityCode.get().ccaaCreation
        !entityCode.get().codeRedeemed
        entityTan.get().tanHash == tanHash
        entityCode.get().codeHash == entityTan.get().codeHash

        where:
        radarCovid | ccaa | number | efgsSharing
        false      | '01' | 1      | true
    }

    @Unroll
    def 'redeem TAN with radarCovid [#radarCovid], ccaa [#ccaa] and number [#number]'(boolean radarCovid, String ccaa, int number) {
        given:
        def codes = generationService.getCodes(radarCovid, ccaa, number)
        def codeDto = new CodeDto()
        codeDto.code = codes.codes.first()

        when:
        def jwt = service.redeemCode(codeDto, false)
        def decodedJWT = JWT.decode(jwt.get())

        def codeHash = hashingService.hash(codes.codes.first())
        def entityCode = repository.findByCodeHashAndCodeRedeemedIsFalse(codeHash)

        def tan = decodedJWT.getClaim('tan').asString()
        def tanHash = hashingService.hash(tan)

        def redeemed = service.redeemTan(tan)
        def entityTan = tanRepository.findByTanHash(tanHash)

        then:
        decodedJWT.subject == codes.codes.first()
        decodedJWT.getClaim('scope').asString() == 'exposed'
        entityCode.get()
        entityCode.get().ccaa == ccaa
        entityCode.get().codeHash == codeHash
        entityCode.get().ccaaCreation
        !entityCode.get().codeRedeemed
        redeemed
        entityTan.empty

        where:
        radarCovid | ccaa | number
        false      | '01' | 1
    }

}
