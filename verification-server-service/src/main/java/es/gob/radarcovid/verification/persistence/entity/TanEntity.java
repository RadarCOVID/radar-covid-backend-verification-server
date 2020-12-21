/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.persistence.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class TanEntity implements Serializable {

	private String tanHash;
    private Date tanValidFrom;
    private Date tanValidUntil;
    private String codeHash;
    
    public boolean tanCanBeRedeemed(Date reference) {
        boolean result = tanValidFrom.before(reference)
                && tanValidUntil.after(reference);
        if (!result) {
            log.warn("Tan can't be redeemed due to date ({})", reference);
        }
        return result;
    }
    
    public boolean tanCanBeRedeemed(LocalDateTime reference) {
        return tanCanBeRedeemed(Date.from(reference.atZone(ZoneOffset.UTC).toInstant()));
    }

}
