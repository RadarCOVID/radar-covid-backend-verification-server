/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.security;

import es.gob.radarcovid.verification.etc.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;

public final class CCAAAuthorizationUtil {

    public static final String getCCAAFromName(String name) {
        return StringUtils.isEmpty(name) ?
                "" : (isRadarCovid(name) ?
                        name.substring(Constants.RADAR_PREFIX_LENGTH, Constants.RADAR_PREFIX_LENGTH + 2) :
                        name.substring(Constants.CCAA_PREFIX_LENGTH, Constants.CCAA_PREFIX_LENGTH + 2));
    }

    public static final String getCCAAFromAuthentication(Authentication authentication) {
        return authentication != null ?
                getCCAAFromName(authentication.getName()) : "";
    }

    public static final boolean isRadarCovid(String name) {
        return StringUtils.isNotEmpty(name) && name.startsWith(Constants.RADAR_PREFIX);
    }

    public static final boolean isRadarCovidAuthentication(Authentication authentication) {
        return authentication != null && isRadarCovid(authentication.getName());
    }

}
