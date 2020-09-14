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

import org.springframework.util.StringUtils;

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
        // Sample code - returns if code is correct
        int last = Character.getNumericValue(validationCode.charAt(validationCode.length() - 1));
        int checksum = checkSum(validationCode.substring(0, validationCode.length() - 1));
        return last == checksum;
    }

    public static String addCheckSum(String input) {
        return input + checkSum(input);
    }

}
