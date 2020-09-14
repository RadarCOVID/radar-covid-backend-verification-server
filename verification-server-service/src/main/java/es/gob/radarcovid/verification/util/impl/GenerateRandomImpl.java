/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.util.impl;

import es.gob.radarcovid.verification.util.CheckSumUtil;
import es.gob.radarcovid.verification.util.GenerateRandom;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Component
public class GenerateRandomImpl implements GenerateRandom {

    private static final SecureRandom SECURE_RANDOM;

    static {
        SecureRandom secureRandom;
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            secureRandom = new SecureRandom();
        }
        SECURE_RANDOM = secureRandom;
    }

    @Override
    public String generateRandomDigits(int size) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < (size - 1); i++) {
           stringBuilder.append(SECURE_RANDOM.nextInt(10));
        }
        String random = stringBuilder.toString();
        return CheckSumUtil.addCheckSum(random);
    }

    @Override
    public String getAlphaNumericString(int size) {
        int lowerLimit = 48; // '0'
        int upperLimit = 122; // 'z'

        return SECURE_RANDOM.ints(lowerLimit, upperLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(size)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
