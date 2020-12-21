/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import es.gob.radarcovid.common.security.KeyVault;
import es.gob.radarcovid.verification.config.SecurityConfiguration;
import es.gob.radarcovid.verification.controller.GenerationController;
import es.gob.radarcovid.verification.domain.CCAADto;
import es.gob.radarcovid.verification.etc.Constants;
import es.gob.radarcovid.verification.etc.RadarCovidProperties;
import es.gob.radarcovid.verification.persistence.CCAAAuthorizationDao;
import es.gob.radarcovid.verification.persistence.CCAADao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final KeyVault keyVault;
    private final CCAADao ccaaDao;
    private final CCAAAuthorizationDao authorizationDao;
    private final HandlerExceptionResolver resolver;
    private final RadarCovidProperties properties;

    public JwtAuthorizationFilter(KeyVault keyVault,
                                  CCAADao dao, CCAAAuthorizationDao authorizationDao,
                                  HandlerExceptionResolver resolver,
                                  RadarCovidProperties properties) {
        this.keyVault = keyVault;
        this.ccaaDao = dao;
        this.authorizationDao = authorizationDao;
        this.resolver = resolver;
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {

            if (existsTokenJWT(request)) {
                Optional<String> optionalCCAA = validateToken(request);
                if (optionalCCAA.isPresent()) {
                    setUpSpringAuthentication(optionalCCAA.get());
                } else {
                    log.warn("No existe información de CCAA");
                    SecurityContextHolder.clearContext();
                }
            } else {
                if (GenerationController.GENERATE_ROUTE.equals(request.getServletPath())) {
                    log.warn("No existe token JWT ({})", request.getServletPath());
                }
                SecurityContextHolder.clearContext();
            }
            chain.doFilter(request, response);

       } catch (AccessDeniedException | JWTVerificationException | KeyVault.PublicKeyNoSuitableEncodingFoundException e) {
            log.error("Excepción leyendo token JWT: {}", e.getMessage());
            resolver.resolveException(request, response, null, e);
        }
    }

    private boolean existsTokenJWT(HttpServletRequest request) {
        String authenticationHeader = request.getHeader(Constants.AUTHORIZATION_HEADER);
        return (!StringUtils.isEmpty(authenticationHeader) && authenticationHeader.startsWith(
                Constants.AUTHORIZATION_PREFIX));
    }

    private Optional<String> validateToken(
            HttpServletRequest request) throws JWTVerificationException, KeyVault.PublicKeyNoSuitableEncodingFoundException {
        String jwtToken = request.getHeader(Constants.AUTHORIZATION_HEADER);
        DecodedJWT decodedJWT = null;
        if (!StringUtils.isEmpty(Constants.AUTHORIZATION_PREFIX)) {
            jwtToken = jwtToken.replace(Constants.AUTHORIZATION_PREFIX, "");
        }
        try {
            decodedJWT = JWT.decode(jwtToken);
        } catch (Exception ex) {
            throw new JWTVerificationException("Token decode error");
        }

        if (checkTokenExpiration(decodedJWT.getIssuedAt(), decodedJWT.getExpiresAt())) {
            String idCCAA = decodedJWT.getSubject();
            MDC.put(Constants.TRACKING, "SUBJECT:" + idCCAA);
            Optional<CCAADto> optionalCCAADto = ccaaDao.findById(idCCAA);
            if (optionalCCAADto.isPresent() && !StringUtils.isEmpty(optionalCCAADto.get().getPublicKey())) {
                String ccaaName = Constants.CCAA_PREFIX + idCCAA;
                KeyPair keyPairCCAA = keyVault.get(ccaaName);
                if (keyPairCCAA == null) {
                    String strPublicKey = KeyVault.getBase64Key(optionalCCAADto.get().getPublicKey());
                    PublicKey publicKey = KeyVault.loadPublicKey(strPublicKey,
                                                                 SecurityConfiguration.PAIR_KEY_ALGORITHM);
                    keyPairCCAA = new KeyPair(publicKey, null);
                    KeyVault.KeyVaultKeyPair keyVaultKeyPair = new KeyVault.KeyVaultKeyPair(ccaaName, keyPairCCAA);
                    keyVault.add(keyVaultKeyPair);
                }

                if (properties.getSubject().equals(idCCAA)) {
                    if (decodedJWT.getIssuer().length() == 8 && decodedJWT.getIssuer().startsWith(
                            Constants.RADAR_PREFIX)) {
                        idCCAA = decodedJWT.getIssuer().substring(6, 8);
                        ccaaName = Constants.RADAR_PREFIX + idCCAA;
                    } else {
                        throw new JWTVerificationException("Subject no es de RadarCOVID " + idCCAA);
                    }
                }

                ECPublicKey publicKey = (ECPublicKey) keyPairCCAA.getPublic();
                Algorithm algorithm = Algorithm.ECDSA512(publicKey, null);
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT jwt = verifier.verify(jwtToken);

                return Optional.of(ccaaName);
            } else {
                throw new JWTVerificationException(idCCAA + " doesn't exist or has no public key");
            }
        } else {
            log.warn("Token de comunidad {} generado con mayor duración (ya expirado): {}", decodedJWT.getSubject(),
                     decodedJWT.getExpiresAt());
        }
        return Optional.empty();
    }

    private void setUpSpringAuthentication(String ccaa) {
        final List<String> authorizationList = new ArrayList<>(List.of(Constants.AUTH_CCAA));
        if (CCAAAuthorizationUtil.isRadarCovid(ccaa)) {
            authorizationList.add(Constants.AUTH_RADARCOVID);
        } else {
            authorizationDao.getAuthorization(CCAAAuthorizationUtil.getCCAAFromName(ccaa))
                    .forEach(auth -> {
                        log.debug("auth = {}", auth.getCode());
                        authorizationList.add(auth.getCode());
                    });
        }
        log.debug("CCAA:{}|AUTHORITIES:{}", ccaa, authorizationList);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(ccaa, null,
                                                                                                          authorizationList.stream().map(
                                                                                                                  SimpleGrantedAuthority::new).collect(
                                                                                                                  Collectors.toList()));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private boolean checkTokenExpiration(Date issuedAt, Date expiresAt) {
        boolean result = false;
        if (issuedAt != null && expiresAt != null) {
            Date now = new Date();

            long issuedAtInMs = issuedAt.getTime();
            Date issuedAtPlusMinutes = new Date(issuedAtInMs + (properties.getMinutes() * 60000) + 1000);

            boolean isBefore = issuedAt.before(expiresAt) && expiresAt.before(issuedAtPlusMinutes);
            result = issuedAt.before(now) && isBefore;
        }
        return result;
    }

}
