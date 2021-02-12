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

import es.gob.radarcovid.verification.util.CheckSumUtil
import spock.lang.Specification
import spock.lang.Unroll

class CheckSumUtilTestSpec extends Specification {

    @Unroll
    def 'checkSum([#input]) = [#result]'(String input, int result) {
        expect:
        CheckSumUtil.checkSum(input) == result

        where:
        input         | result
        '87423800248' | 6
        '14470714152' | 2
        '41032291514' | 2
    }

    @Unroll
    def 'validateChecksum([#input]) = [#result]'(String input, boolean result) {
        expect:
        CheckSumUtil.validateChecksum(input) == result

        where:
        input          | result
        '687423800248' | true
        '214470714152' | true
        '241032291514' | true
        '112358132134' | true
    }

}
