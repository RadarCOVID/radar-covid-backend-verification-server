/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.util.impl.test;

import es.gob.radarcovid.verification.util.HashingService;
import es.gob.radarcovid.verification.util.impl.HashingServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class HashingServiceTest {

    private static final List<String> codes = Arrays.asList(
            "209923975712",
            "034102898740",
            "766294655301",
            "222311755475",
            "892548489472"
    );

    @Test
    public void testHash() {
        HashingService hashingService = new HashingServiceImpl();

        log.info("# codes = {}", codes.size());
        codes.stream().forEach(code -> {
            log.info("{} -> {}", code, hashingService.hash(code));
        });
    }
}
