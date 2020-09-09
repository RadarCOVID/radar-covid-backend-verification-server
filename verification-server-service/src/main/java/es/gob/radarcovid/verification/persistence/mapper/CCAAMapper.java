/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.persistence.mapper;

import es.gob.radarcovid.verification.domain.CCAADto;
import es.gob.radarcovid.verification.persistence.entity.CCAAEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CCAAMapper {

    CCAADto asDto(CCAAEntity entity);

    CCAAEntity asEntity(CCAADto dto);

}
