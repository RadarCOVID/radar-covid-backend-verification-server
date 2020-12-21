/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.persistence.vo;

import es.gob.radarcovid.verification.etc.Constants;
import lombok.Getter;

public enum AuthorizationEnum {

    CCAA(Constants.AUTH_CCAA),
    GENERATION(Constants.AUTH_GENERATION),
    KPI(Constants.AUTH_KPI);

    @Getter
    private String code;

    AuthorizationEnum(String code) {
        this.code = code;
    }

}
