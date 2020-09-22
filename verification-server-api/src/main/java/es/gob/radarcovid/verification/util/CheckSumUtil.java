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


import es.gob.radarcovid.verification.validation.impl.CodeValidator;
import org.apache.commons.lang3.StringUtils;

public class CheckSumUtil {

    private CheckSumUtil() {}

    public static int checkSum(String input) {
        // Sample code - returns checkSum from input
        int result = 0;
        if (!StringUtils.isEmpty(input)) {
            result = Character.getNumericValue(input.charAt(input.length() - 1));
        }
        return result;
    }

    public static boolean validateChecksum(String validationCode) {
        boolean result = false;
        if (StringUtils.isNotEmpty(validationCode)) {
            if (CodeValidator.FAKE_CODE.equals(validationCode)) {
                return true;
            } else {
                // Sample code - returns if code is correct
                int last = Character.getNumericValue(validationCode.charAt(validationCode.length() - 1));
                int checksum = checkSum(validationCode.substring(0, validationCode.length() - 1));
                result = (last == checksum);
            }
        }
        return result;
    }

    public static String addCheckSum(String input) {
        return input + checkSum(input);
    }

}
