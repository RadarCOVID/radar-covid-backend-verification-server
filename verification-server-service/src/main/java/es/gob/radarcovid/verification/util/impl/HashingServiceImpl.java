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

import es.gob.radarcovid.verification.util.HashingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HashingServiceImpl implements HashingService {

    @Override
    public String hash(String toHash) {
        return DigestUtils.sha256Hex(toHash);
    }
}
