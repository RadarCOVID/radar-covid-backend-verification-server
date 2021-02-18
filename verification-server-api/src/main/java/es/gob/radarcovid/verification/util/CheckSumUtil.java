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
        int evens = 0; // initialize evens variable
        int odds = 0; // initialize odds variable
        for (int i = 0; i < input.length(); i++) {
            //check if number is odd or even
            if ((int) input.charAt(i) % 2 == 0) { // check that the character at position "i" is divisible by 2 which means it's even
                evens += (int) input.charAt(i); // then add it to the evens
            } else {
                odds += (int) input.charAt(i); // else add it to the odds
            }
        }
        evens = evens * 2; // multiply evens by two
        odds = odds * 3; // multiply odds by three
        int total = odds + evens; // sum odds and evens
        return (total % 10 == 0) ? 0 : 10 - (total % 10);
    }

    public static boolean validateChecksum(String validationCode) {
        boolean result = false;
        if (StringUtils.isNotEmpty(validationCode)) {
            if (CodeValidator.FAKE_CODE.equals(validationCode)) {
                result = true;
            } else {
                int first = Character.getNumericValue(validationCode.charAt(0));
                int checksum = checkSum(validationCode.substring(1, validationCode.length()));
                result = (first == checksum);
            }
        }
        return result;
    }

    public static String addCheckSum(String input) {
        return checkSum(input) + input;
    }

}
