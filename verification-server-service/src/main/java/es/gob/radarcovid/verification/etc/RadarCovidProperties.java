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

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class RadarCovidProperties {
	
	@Value("${application.jwt.subject}")
    private String subject;
	
	@Value("${application.jwt.issuer}")
    private String issuer;
	
	@Value("${application.jwt.minutes}")
    private long minutes;
}
