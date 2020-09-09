/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.persistence;

import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface VerificationDao {

    boolean isValidCode(String code);

    @Transactional
    boolean redeemCode(String code, String tan, Date validUntil);

    @Transactional
    boolean redeemTan(String tan);

    @Transactional
    boolean saveCodes(boolean radarCovid, String ccaa, List<String> codes, Date validUntil);

}
