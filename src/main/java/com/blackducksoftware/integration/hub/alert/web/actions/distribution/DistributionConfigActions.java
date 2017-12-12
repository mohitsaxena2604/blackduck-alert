/**
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.alert.web.actions.distribution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;

import com.blackducksoftware.integration.hub.alert.datasource.entity.CommonDistributionConfigEntity;
import com.blackducksoftware.integration.hub.alert.datasource.entity.ConfiguredProjectEntity;
import com.blackducksoftware.integration.hub.alert.datasource.entity.DatabaseEntity;
import com.blackducksoftware.integration.hub.alert.datasource.entity.repository.CommonDistributionRepository;
import com.blackducksoftware.integration.hub.alert.datasource.entity.repository.ConfiguredProjectsRepository;
import com.blackducksoftware.integration.hub.alert.datasource.relation.DistributionProjectRelation;
import com.blackducksoftware.integration.hub.alert.datasource.relation.repository.DistributionProjectRepository;
import com.blackducksoftware.integration.hub.alert.exception.AlertException;
import com.blackducksoftware.integration.hub.alert.exception.AlertFieldException;
import com.blackducksoftware.integration.hub.alert.web.ObjectTransformer;
import com.blackducksoftware.integration.hub.alert.web.actions.ConfigActions;
import com.blackducksoftware.integration.hub.alert.web.model.distribution.CommonDistributionConfigRestModel;

public abstract class DistributionConfigActions<D extends DatabaseEntity, R extends CommonDistributionConfigRestModel> extends ConfigActions<D, R> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected final CommonDistributionRepository commonDistributionRepository;
    protected final ConfiguredProjectsRepository configuredProjectsRepository;
    protected final DistributionProjectRepository distributionProjectRepository;
    protected final JpaRepository<D, Long> channelDistributionRepository;
    protected final ObjectTransformer objectTransformer;

    public DistributionConfigActions(final Class<D> databaseEntityClass, final Class<R> configRestModelClass, final CommonDistributionRepository commonDistributionRepository, final ConfiguredProjectsRepository configuredProjectsRepository,
            final DistributionProjectRepository distributionProjectRepository, final JpaRepository<D, Long> channelDistributionRepository, final ObjectTransformer objectTransformer) {
        super(databaseEntityClass, configRestModelClass, channelDistributionRepository, objectTransformer);
        this.commonDistributionRepository = commonDistributionRepository;
        this.configuredProjectsRepository = configuredProjectsRepository;
        this.distributionProjectRepository = distributionProjectRepository;
        this.channelDistributionRepository = channelDistributionRepository;
        this.objectTransformer = objectTransformer;
    }

    @Override
    public List<R> getConfig(final Long id) throws AlertException {
        if (id != null) {
            final D foundEntity = channelDistributionRepository.findOne(id);
            if (foundEntity != null) {
                return Arrays.asList(constructRestModel(foundEntity));
            }
            return Collections.emptyList();
        }
        return constructRestModels();
    }

    @Override
    public D saveConfig(final R restModel) throws AlertException {
        if (restModel != null) {
            try {
                D createdEntity = objectTransformer.configRestModelToDatabaseEntity(restModel, databaseEntityClass);
                CommonDistributionConfigEntity commonEntity = objectTransformer.configRestModelToDatabaseEntity(restModel, CommonDistributionConfigEntity.class);
                if (createdEntity != null && commonEntity != null) {
                    createdEntity = channelDistributionRepository.save(createdEntity);
                    commonEntity.setDistributionConfigId(createdEntity.getId());
                    commonEntity = commonDistributionRepository.save(commonEntity);
                    saveConfiguredProjects(commonEntity, restModel);
                    return createdEntity;
                }
            } catch (final Exception e) {
                throw new AlertException(e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    public D saveNewConfigUpdateFromSavedConfig(final R restModel) throws AlertException {
        return saveConfig(restModel);
    }

    @Override
    public void deleteConfig(final Long id) {
        if (id != null) {
            final CommonDistributionConfigEntity commonEntity = commonDistributionRepository.findOne(id);
            if (commonEntity != null) {
                final Long distributionConfigId = commonEntity.getDistributionConfigId();
                channelDistributionRepository.delete(distributionConfigId);
                commonDistributionRepository.delete(id);
            }
            cleanUpConfiguredProjects();
        }
    }

    @Override
    public String validateConfig(final R restModel) throws AlertFieldException {
        final Map<String, String> fieldErrors = new HashMap<>();
        if (StringUtils.isNotBlank(restModel.getId()) && !StringUtils.isNumeric(restModel.getId())) {
            fieldErrors.put("id", "Not an Integer.");
        }
        if (StringUtils.isNotBlank(restModel.getDistributionConfigId()) && !StringUtils.isNumeric(restModel.getDistributionConfigId())) {
            fieldErrors.put("distributionConfigId", "Not an Integer.");
        }
        if (StringUtils.isNotBlank(restModel.getFilterByProject()) && !isBoolean(restModel.getFilterByProject())) {
            fieldErrors.put("filterByProject", "Not a Boolean.");
        }
        if (!fieldErrors.isEmpty()) {
            throw new AlertFieldException(fieldErrors);
        }
        return "Valid";
    }

    @Override
    public List<String> sensitiveFields() {
        return Collections.emptyList();
    }

    protected void saveConfiguredProjects(final CommonDistributionConfigEntity commonEntity, final R restModel) {
        if (Boolean.TRUE.equals(commonEntity.getFilterByProject())) {
            final List<String> configuredProjectsFromUI = restModel.getConfiguredProjects();
            if (configuredProjectsFromUI != null) {
                // Remove the old DistributionProjectRelations for the distribution configuration:
                final List<DistributionProjectRelation> distributionProjects = distributionProjectRepository.findByCommonDistributionConfigId(commonEntity.getId());
                distributionProjectRepository.delete(distributionProjects);
                // Add new DistributionProjectRelations (and ConfiguredProjectEntities if needed):
                for (final String projectName : configuredProjectsFromUI) {
                    Long projectId;
                    final ConfiguredProjectEntity foundEntity = configuredProjectsRepository.findByProjectName(projectName);
                    if (foundEntity != null) {
                        projectId = foundEntity.getId();
                    } else {
                        final ConfiguredProjectEntity createdEntity = configuredProjectsRepository.save(new ConfiguredProjectEntity(projectName));
                        projectId = createdEntity.getId();
                    }
                    distributionProjectRepository.save(new DistributionProjectRelation(commonEntity.getId(), projectId));
                }
                cleanUpConfiguredProjects();
            }
            logger.warn("{}: List of configured projects was null; configured projects will not be updated.", commonEntity.getName());
        }
    }

    protected List<String> getConfiguredProjects(final CommonDistributionConfigEntity commonEntity) {
        final List<DistributionProjectRelation> distributionProjects = distributionProjectRepository.findByCommonDistributionConfigId(commonEntity.getId());
        final List<String> configuredProjects = new ArrayList<>();
        for (final DistributionProjectRelation relation : distributionProjects) {
            final ConfiguredProjectEntity entity = configuredProjectsRepository.findOne(relation.getProjectId());
            configuredProjects.add(entity.getProjectName());
        }
        return configuredProjects;
    }

    protected void cleanUpConfiguredProjects() {
        final List<ConfiguredProjectEntity> configuredProjects = configuredProjectsRepository.findAll();
        configuredProjects.forEach(configuredProject -> {
            final List<DistributionProjectRelation> distributionProjects = distributionProjectRepository.findByProjectId(configuredProject.getId());
            if (distributionProjects.isEmpty()) {
                configuredProjectsRepository.delete(configuredProject);
            }
        });
    }

    public List<R> constructRestModels() {
        final List<D> allEntities = channelDistributionRepository.findAll();
        final List<R> constructedRestModels = new ArrayList<>();
        for (final D entity : allEntities) {
            try {
                final R restModel = constructRestModel(entity);
                if (restModel != null) {
                    constructedRestModels.add(restModel);
                } else {
                    logger.warn("Entity did not exist");
                }
            } catch (final AlertException e) {
                logger.warn("Problem constructing rest model", e);
            }
        }
        return constructedRestModels;
    }

    public R constructRestModel(final D entity) throws AlertException {
        final D distributionEntity = channelDistributionRepository.findOne(entity.getId());
        final CommonDistributionConfigEntity commonEntity = commonDistributionRepository.findByDistributionConfigIdAndDistributionType(entity.getId(), getDistributionName());
        if (distributionEntity != null && commonEntity != null) {
            final R restModel = constructRestModel(commonEntity, distributionEntity);
            restModel.setConfiguredProjects(getConfiguredProjects(commonEntity));
            return restModel;
        }
        return null;
    }

    public abstract String getDistributionName();

    public abstract R constructRestModel(final CommonDistributionConfigEntity commonEntity, D distributionEntity) throws AlertException;

}
