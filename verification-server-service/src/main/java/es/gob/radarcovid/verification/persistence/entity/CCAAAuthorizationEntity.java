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

import es.gob.radarcovid.verification.persistence.vo.AuthorizationEnum;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "CCAA_AUTH")
@Data
public class CCAAAuthorizationEntity {

    @Id
    @Column(name = "NM_ID_CCAA_AUTH")
    private Long id;

    @Version
    @Column(name = "NM_VERSION")
    private Long version;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FC_CREATION_DATE")
    private Date createdAt;

    @Column(name = "DE_CCAA_ID")
    private String ccaa;

    @Enumerated(EnumType.STRING)
    @Column(name = "DE_AUTH")
    private AuthorizationEnum authorization;

}
