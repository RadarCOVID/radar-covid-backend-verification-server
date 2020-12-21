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

import es.gob.radarcovid.verification.persistence.CCAAAuthorizationDao;
import es.gob.radarcovid.verification.persistence.repository.CCAAAuthorizationRepository;
import es.gob.radarcovid.verification.persistence.vo.AuthorizationEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CCAAAuthorizationDaoImpl implements CCAAAuthorizationDao {

    private final CCAAAuthorizationRepository repository;

    @Override
    public List<AuthorizationEnum> getAuthorization(String ccaa) {
        return repository.findByCcaa(ccaa).map(auth -> auth.getAuthorization()).collect(Collectors.toList());
    }
}
