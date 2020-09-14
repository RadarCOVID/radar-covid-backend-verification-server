/**
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.persistence.test

import es.gob.radarcovid.verification.persistence.CCAADao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles("test")
class CCAADaoTestSpec extends Specification {

    @Autowired
    CCAADao ccaaDao

    def "find all CCAA"() {
        given:
        def ccaaList = ccaaDao.list

        expect:
        ccaaList.size() == 20
    }

    def "find By Id"(String id, String ccaaName) {
        expect:
        ccaaDao.findById(id).get().name == ccaaName

        where:
        id   | ccaaName
        "03" | "Asturias, Principado de"
        "04" | "Balears, Illes"
        "05" | "Canarias"
    }
}
