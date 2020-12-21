/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.persistence.repository;

import es.gob.radarcovid.verification.persistence.entity.CCAAAuthorizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface CCAAAuthorizationRepository extends JpaRepository <CCAAAuthorizationEntity, Long> {

    Stream<CCAAAuthorizationEntity> findByCcaa(String ccaa);

}
