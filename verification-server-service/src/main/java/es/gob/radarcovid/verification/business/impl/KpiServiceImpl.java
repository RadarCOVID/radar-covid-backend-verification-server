/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.business.impl;

import es.gob.radarcovid.verification.api.KpiDto;
import es.gob.radarcovid.verification.business.KpiService;
import es.gob.radarcovid.verification.persistence.CCAAKpiDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KpiServiceImpl implements KpiService {

    private final CCAAKpiDao kpiDao;

    @Override
    public void saveKpi(boolean radarCovid, String ccaa, List<KpiDto> kpiDtoList) {
        kpiDao.saveKpi(radarCovid, ccaa, kpiDtoList);
    }

}
