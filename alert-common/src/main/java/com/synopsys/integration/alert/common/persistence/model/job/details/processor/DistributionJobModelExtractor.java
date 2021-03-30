/*
 * alert-common
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.common.persistence.model.job.details.processor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.common.descriptor.action.DescriptorActionMap;
import com.synopsys.integration.alert.common.descriptor.config.ui.ChannelDistributionUIConfig;
import com.synopsys.integration.alert.common.descriptor.config.ui.ProviderDistributionUIConfig;
import com.synopsys.integration.alert.common.persistence.model.ConfigurationFieldModel;
import com.synopsys.integration.alert.common.persistence.model.job.BlackDuckProjectDetailsModel;
import com.synopsys.integration.alert.common.persistence.model.job.DistributionJobModel;
import com.synopsys.integration.alert.common.persistence.model.job.DistributionJobModelBuilder;
import com.synopsys.integration.alert.common.persistence.model.job.details.DistributionJobDetailsModel;

@Component
public final class DistributionJobModelExtractor {
    private final DistributionJobFieldExtractor distributionJobFieldExtractor;
    private final DescriptorActionMap<DistributionJobDetailsExtractor> jobDetailsExtractorMap;

    @Autowired
    public DistributionJobModelExtractor(DistributionJobFieldExtractor distributionJobFieldExtractor, List<DistributionJobDetailsExtractor> jobDetailsExtractors) {
        this.distributionJobFieldExtractor = distributionJobFieldExtractor;
        this.jobDetailsExtractorMap = new DescriptorActionMap<>(jobDetailsExtractors);
    }

    public final DistributionJobModel convertToJobModel(
        UUID jobId,
        Map<String, ConfigurationFieldModel> configuredFieldsMap,
        OffsetDateTime createdAt,
        @Nullable OffsetDateTime lastUpdated,
        List<BlackDuckProjectDetailsModel> projectFilterDetails
    ) {
        String channelDescriptorName = distributionJobFieldExtractor.extractFieldValueOrEmptyString(ChannelDistributionUIConfig.KEY_CHANNEL_NAME, configuredFieldsMap);
        DistributionJobModelBuilder builder = DistributionJobModel.builder()
                                                  .jobId(jobId)
                                                  .enabled(distributionJobFieldExtractor.extractFieldValue(ChannelDistributionUIConfig.KEY_ENABLED, configuredFieldsMap).map(Boolean::valueOf).orElse(true))
                                                  .name(distributionJobFieldExtractor.extractFieldValueOrEmptyString(ChannelDistributionUIConfig.KEY_NAME, configuredFieldsMap))
                                                  .distributionFrequency(distributionJobFieldExtractor.extractFieldValueOrEmptyString(ChannelDistributionUIConfig.KEY_FREQUENCY, configuredFieldsMap))
                                                  .processingType(distributionJobFieldExtractor.extractFieldValueOrEmptyString(ProviderDistributionUIConfig.KEY_PROCESSING_TYPE, configuredFieldsMap))
                                                  .channelDescriptorName(channelDescriptorName)
                                                  .createdAt(createdAt)
                                                  .lastUpdated(lastUpdated)

                                                  .blackDuckGlobalConfigId(distributionJobFieldExtractor.extractFieldValue(ProviderDistributionUIConfig.KEY_COMMON_CONFIG_ID, configuredFieldsMap).map(Long::valueOf).orElse(-1L))
                                                  .filterByProject(distributionJobFieldExtractor.extractFieldValue(ProviderDistributionUIConfig.KEY_FILTER_BY_PROJECT, configuredFieldsMap).map(Boolean::valueOf).orElse(false))
                                                  .projectNamePattern(distributionJobFieldExtractor.extractFieldValue(ProviderDistributionUIConfig.KEY_PROJECT_NAME_PATTERN, configuredFieldsMap).orElse(null))
                                                  .notificationTypes(distributionJobFieldExtractor.extractFieldValues(ProviderDistributionUIConfig.KEY_NOTIFICATION_TYPES, configuredFieldsMap))
                                                  .policyFilterPolicyNames(distributionJobFieldExtractor.extractFieldValues("blackduck.policy.notification.filter", configuredFieldsMap))
                                                  .vulnerabilityFilterSeverityNames(distributionJobFieldExtractor.extractFieldValues("blackduck.vulnerability.notification.filter", configuredFieldsMap))
                                                  .projectFilterDetails(projectFilterDetails);

        DistributionJobDetailsExtractor extractor = jobDetailsExtractorMap.findRequiredAction(channelDescriptorName);
        DistributionJobDetailsModel distributionJobDetailsModel = extractor.extractDetails(jobId, configuredFieldsMap);
        builder.distributionJobDetails(distributionJobDetailsModel);

        return builder.build();
    }

}
