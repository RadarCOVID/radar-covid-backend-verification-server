/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.business;

import es.gob.radarcovid.verification.api.KpiDto;

import java.util.List;

public interface KpiService {
    void saveKpi(boolean radarCovid, String ccaa, List<KpiDto> kpiDtoList);
}
