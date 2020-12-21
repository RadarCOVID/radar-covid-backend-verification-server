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

import es.gob.radarcovid.verification.vo.CCAAKpiTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CCAA_KPI")
public class CCAAKpiEntity {

    private static final String SEQUENCE_NAME = "SQ_NM_ID_CCAA_KPI";

    @Id
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @Column(name = "NM_ID_CCAA_KPI")
    private Long id;

    @Version
    @Column(name = "NM_VERSION")
    private Long version;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FC_CREATION_DATE")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FC_UPDATE_DATE")
    private Date updatedAt;

    @Column(name = "DE_CCAA_ID")
    private String ccaa;

    @Column(name = "IN_CCAA_CREATION")
    private boolean ccaaCreation;

    @Temporal(TemporalType.DATE)
    @Column(name = "FC_KPI_DATE")
    private Date kpiDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "DE_KPI_TYPE")
    private CCAAKpiTypeEnum kpiType;

    @Column(name = "NM_KPI_VALUE")
    private Integer kpiValue;

}
