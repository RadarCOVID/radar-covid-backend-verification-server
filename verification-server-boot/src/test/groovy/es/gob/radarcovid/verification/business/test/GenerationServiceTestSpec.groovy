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


import es.gob.radarcovid.verification.business.GenerationService
import es.gob.radarcovid.verification.persistence.repository.CCAAKpiRepository
import es.gob.radarcovid.verification.persistence.repository.VerificationRepository
import es.gob.radarcovid.verification.util.HashingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification
import spock.lang.Unroll

@SpringBootTest
@ActiveProfiles('test')
class GenerationServiceTestSpec extends Specification {

    @Autowired
    GenerationService service

    @Autowired
    CCAAKpiRepository redemptionRepository

    @Autowired
    HashingService hashingService

    @Autowired
    VerificationRepository verificationRepository

    @Unroll
    def 'get codes with radarCovid [#radarCovid], ccaa [#ccaa] and number [#number]'(boolean radarCovid, String ccaa, int number) {
        when:
        def codes = service.getCodes(radarCovid, ccaa, number)
        def hash = hashingService.hash(codes.codes.first())
        def entity = verificationRepository.findByCodeHashAndCodeRedeemedIsFalse(hash)

        then:
        codes.codes.size() == number
        entity.get().ccaa == ccaa
        entity.get().codeHash == hash
        entity.get().ccaaCreation
        !entity.get().codeRedeemed

        where:
        radarCovid | ccaa | number
        false      | '01' | 1
    }

}
