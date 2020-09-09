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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TAN")
public class VerificationEntity implements Serializable {

    private static final String SEQUENCE_NAME = "SQ_NM_ID_TAN";

    @Id
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @Column(name = "NM_ID_TAN")
    private Long id;

    @Version
    @Column(name = "NM_VERSION")
    private Long version;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FC_CREATION_DATE")
    private Date createdAt;

    @Column(name = "DE_CCAA_ID")
    private String ccaa;

    @Column(name = "IN_CCAA_CREATION")
    private boolean ccaaCreation;

    @Temporal(TemporalType.DATE)
    @Column(name = "FC_CODE_VALID_FROM")
    private Date codeValidFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "FC_CODE_VALID_UNTIL")
    private Date codeValidUntil;

    @Column(name = "DE_CODE_HASH")
    private String codeHash;

    @Column(name = "IN_CODE_REDEEMED")
    private boolean codeRedeemed;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FC_CODE_REDEEMED_DATE")
    private Date codeRedeemedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FC_TAN_VALID_FROM")
    private Date tanValidFrom;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FC_TAN_VALID_UNTIL")
    private Date tanValidUntil;

    @Column(name = "DE_TAN_HASH")
    private String tanHash;

    @Column(name = "IN_TAN_REDEEMED")
    private boolean tanRedeemed;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FC_TAN_REDEEMED_DATE")
    private Date tanRedeemedAt;

    /**
     * Check if the code can be redeemed by date.
     *
     * @param reference the date to check if it is in between from and until range
     * @return true or false if it can be redeemed
     */
    public boolean codeCanBeRedeemed(Date reference) {
        return codeValidFrom.before(reference)
                && codeValidUntil.after(reference)
                && !isCodeRedeemed();
    }

    public boolean codeCanBeRedeemed(LocalDateTime reference) {
        return codeCanBeRedeemed(Date.from(reference.atZone(ZoneOffset.UTC).toInstant()));
    }

    public boolean tanCanBeRedeemed(Date reference) {
        return tanValidFrom.before(reference)
                && tanValidUntil.after(reference)
                && !isCodeRedeemed()
                && !isTanRedeemed();
    }

    public boolean tanCanBeRedeemed(LocalDateTime reference) {
        return tanCanBeRedeemed(Date.from(reference.atZone(ZoneOffset.UTC).toInstant()));
    }

}
