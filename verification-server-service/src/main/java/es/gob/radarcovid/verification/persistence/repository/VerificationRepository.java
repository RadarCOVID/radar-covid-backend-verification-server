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

import es.gob.radarcovid.verification.persistence.entity.VerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<VerificationEntity, Long> {

    /**
     * This method looks in the Database for an if a VerificationEntity exists for the tan hash.
     *
     * @param codeHash hash to search for
     * @return Optional VerificationEntity
     */
    Optional<VerificationEntity> findByCodeHashAndCodeRedeemedIsFalse(String codeHash);

}
