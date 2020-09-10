/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class CheckSumUtil {

    private CheckSumUtil() {}

    public static int checkSum(String input) {
        // Sample code - returns checkSum from input
        int result = 0;
        if (input != null && !StringUtils.isEmpty(input)) {
            result = Character.getNumericValue(input.charAt(input.length() - 1));
        }
        return result;
    }

    public static boolean validateChecksum(String validationCode) {
        String dummy = validationCode.substring(0, validationCode.length() - 1)
        return true;
    }

}
