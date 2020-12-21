/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.persistence.impl;

import es.gob.radarcovid.verification.api.KpiDto;
import es.gob.radarcovid.verification.persistence.CCAAKpiDao;
import es.gob.radarcovid.verification.persistence.entity.CCAAKpiEntity;
import es.gob.radarcovid.verification.persistence.repository.CCAAKpiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CCAAKpiDaoImpl implements CCAAKpiDao {

    private final CCAAKpiRepository repository;

    @Override
    public void saveKpi(boolean radarCovid, String ccaa, List<KpiDto> kpiDtoList) {
        if (StringUtils.isNotEmpty(ccaa) && kpiDtoList != null && kpiDtoList.size() > 0) {
            List<CCAAKpiEntity> entities = new ArrayList<>();
            Date now = new Date();
            kpiDtoList.stream().forEach(kpi -> {
                Optional<CCAAKpiEntity> entityExists = repository.findByCcaaAndKpiTypeAndKpiDate(ccaa,
                                                                                                 kpi.getType(),
                                                                                                 kpi.getDate());
                CCAAKpiEntity entity;
                if (entityExists.isPresent()) {
                    entity = entityExists.get();
                    entity.setKpiValue(entity.getKpiValue() + kpi.getValue());
                    entity.setUpdatedAt(now);
                } else {
                    entity = new CCAAKpiEntity();
                    entity.setCcaa(ccaa);
                    entity.setCcaaCreation(!radarCovid);
                    entity.setCreatedAt(now);
                    entity.setKpiDate(kpi.getDate());
                    entity.setKpiType(kpi.getType());
                    entity.setKpiValue(kpi.getValue());
                }
                entities.add(entity);
            });
            repository.saveAll(entities);
        }
    }
}
