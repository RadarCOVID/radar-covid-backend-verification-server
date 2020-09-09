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

import es.gob.radarcovid.verification.domain.CCAADto;
import es.gob.radarcovid.verification.persistence.CCAADao;
import es.gob.radarcovid.verification.persistence.mapper.CCAAMapper;
import es.gob.radarcovid.verification.persistence.repository.CCAARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CCAADaoImpl implements CCAADao {

    private final CCAARepository repository;
    private final CCAAMapper mapper;

    @Override
    public List<CCAADto> getList() {
        return repository.findAll().stream().map(mapper::asDto).collect(Collectors.toList());
    }

    @Override
    public Optional<CCAADto> findById(String id) {
        return repository.findById(id).map(mapper::asDto);
    }

}
