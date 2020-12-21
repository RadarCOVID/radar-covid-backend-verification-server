/**
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.business.test

import es.gob.radarcovid.verification.api.KpiDto
import es.gob.radarcovid.verification.business.KpiService
import es.gob.radarcovid.verification.persistence.repository.CCAAKpiRepository
import es.gob.radarcovid.verification.vo.CCAAKpiTypeEnum
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate
import java.time.ZoneOffset

@SpringBootTest
@ActiveProfiles('test')
class KpiServiceTestSpec extends Specification {

    @Autowired
    KpiService service

    @Autowired
    CCAAKpiRepository repository

    @Unroll
    def 'save kpi(#radarCovid, #ccaa, #kpiDateStr, #kpiType, #kpiValue)'(boolean radarCovid, String ccaa, String kpiDateStr, CCAAKpiTypeEnum kpiType, Integer kpiValue) {
        given:
        def kpiDate = Date.from(LocalDate.parse(kpiDateStr).atStartOfDay(ZoneOffset.UTC).toInstant())
        def kpi = new KpiDto(kpiDate, kpiType, kpiValue)
        List<KpiDto> kpiList = new ArrayList<>()
        kpiList.add(kpi)

        when:
        service.saveKpi(radarCovid, ccaa, kpiList)
        def saved = repository.findByCcaaAndKpiTypeAndKpiDate(ccaa, kpiType, kpiDate)

        then:
        saved.isPresent()
        saved.get().ccaa == ccaa
        saved.get().kpiType == kpiType
        saved.get().kpiValue == kpiValue
        saved.get().kpiDate.toString() == kpiDateStr

        where:
        radarCovid | ccaa | kpiDateStr   | kpiType                   | kpiValue
        false      | '01' | '2020-11-22' | CCAAKpiTypeEnum.CONTAGIOS | 5
    }

}
