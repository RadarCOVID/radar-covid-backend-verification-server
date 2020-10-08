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

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import es.gob.radarcovid.verification.util.DateInfection;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class DateInfectionImpl implements DateInfection {

    @Value("${application.retention-days}")
    private Integer retentionDays;

    @Value("${application.jwt.onset.default}")
    private int onsetDefaultDays;

    @Value("${application.jwt.onset.app}")
    private int onsetAppDays;
    
    @Override
    public Date getDefaultInfectionDate() {
    	return Date.from(LocalDate.now().minusDays(onsetDefaultDays).atStartOfDay(ZoneOffset.UTC).toInstant());
    }
    
    @Override
    public Date getInfectionDate(Date date) {
    	LocalDate result = LocalDate.now().minusDays(retentionDays);
        if (date == null) {
        	result = LocalDate.now().minusDays(onsetDefaultDays);
        } else {
        	
            LocalDate localDateEntry = date.toInstant().atZone(ZoneOffset.UTC).toLocalDate().minusDays(onsetAppDays);
            LocalDate nowRetentionDaysOnset = LocalDate.now().minusDays(retentionDays).plusDays(onsetAppDays);

            if (localDateEntry.isBefore(result)) {
                log.warn("Fecha {} debe ser al menos {}", date, nowRetentionDaysOnset);
            } else {
            	result = LocalDate.ofInstant(date.toInstant(), ZoneOffset.UTC).minusDays(onsetAppDays);
            }
        }
    	return Date.from(result.atStartOfDay(ZoneOffset.UTC).toInstant());
    }
}
