/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.etc;

public class Constants {

    public static final String TRACKING = "TRACKING";

    public static final String AUTH_CCAA = "ROLE_CCAA";
    public static final String AUTH_GENERATION = "ROLE_GENERATION";
    public static final String AUTH_KPI = "ROLE_KPI";
    public static final String AUTH_RADARCOVID = "ROLE_RADARCOVID";

    public static final String API_KEY_AUTH = "apiKeyAuth";

    public static final String AUTHORIZATION_HEADER = "X-RadarCovid-Authorization";
    public static final String AUTHORIZATION_PREFIX = "Bearer ";

    public static final String RADAR_PREFIX = "radar-";
    public static final String CCAA_PREFIX = "ccaa-";
    public static final int RADAR_PREFIX_LENGTH = RADAR_PREFIX.length();
    public static final int CCAA_PREFIX_LENGTH = CCAA_PREFIX.length();

}
