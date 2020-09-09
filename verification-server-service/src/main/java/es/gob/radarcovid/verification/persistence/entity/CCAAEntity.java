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

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "CCAA")
@Data
public class CCAAEntity implements Serializable {

    @Id
    @Column(name = "DE_CCAA_ID")
    private String id;

    @Column(name = "DE_CCAA_NAME")
    private String name;

    @Column(name = "DE_CCAA_PUBLIC_KEY")
    private String publicKey;

    @Column(name = "DE_CCAA_ISSUER")
    private String issuer;

}
