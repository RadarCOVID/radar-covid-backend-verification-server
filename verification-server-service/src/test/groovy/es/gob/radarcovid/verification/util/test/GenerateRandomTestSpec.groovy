/**
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.util.test

import es.gob.radarcovid.verification.util.GenerateRandom
import es.gob.radarcovid.verification.util.impl.GenerateRandomImpl
import spock.lang.Specification

class GenerateRandomTestSpec extends Specification {

    private GenerateRandom generateRandom = new GenerateRandomImpl()

    def "generateRandomDigits"(int number, int result) {
        expect:
        generateRandom.generateRandomDigits(number).length() == result

        where:
        number | result
        5      | 5
        12     | 12
    }

    def "getAlphaNumericString"(int number, int result) {
        expect:
        generateRandom.getAlphaNumericString(number).length() == result

        where:
        number | result
        5      | 5
        12     | 12
    }

}
