/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.persistence;

import es.gob.radarcovid.verification.domain.CCAADto;

import java.util.List;
import java.util.Optional;

public interface CCAADao {

//    @Cacheable(CacheConstants.CACHE_CCAA_LIST)
    List<CCAADto> getList();

//    @Cacheable(CacheConstants.CACHE_CCAA_ID)
    Optional<CCAADto> findById(String id);

}
