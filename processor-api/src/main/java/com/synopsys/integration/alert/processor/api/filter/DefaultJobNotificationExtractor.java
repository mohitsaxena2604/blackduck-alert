/**
 * processor-api
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.alert.processor.api.filter;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.common.enumeration.FrequencyType;
import com.synopsys.integration.alert.common.persistence.accessor.JobAccessor;
import com.synopsys.integration.alert.common.persistence.model.job.FilteredDistributionJobModel;
import com.synopsys.integration.alert.common.persistence.model.job.FilteredDistributionJobRequestModel;
import com.synopsys.integration.alert.processor.api.filter.model.FilterableNotificationWrapper;
import com.synopsys.integration.alert.processor.api.filter.model.NotificationFilterMapModel;
import com.synopsys.integration.blackduck.api.manual.enumeration.NotificationType;
import com.synopsys.integration.datastructure.SetMap;

@Component
public class DefaultJobNotificationExtractor implements JobNotificationExtractor {
    private JobAccessor jobAccessor;

    @Autowired
    public DefaultJobNotificationExtractor(JobAccessor jobAccessor) {
        this.jobAccessor = jobAccessor;
    }

    /*
     * Filter Items:
     * Frequency (Passed into processor)
     * Notification Type (From notification)
     * Filter By Project (Projects from notification if applicable)
     *   Project Name
     *   Project Name Pattern
     * Filter by Vulnerability severity (From notification if applicable)
     * Filter by Policy name (From notification if applicable)
     */

    @Override
    public Map<NotificationFilterMapModel, List<FilterableNotificationWrapper<?>>> mapJobsToNotifications(List<? extends FilterableNotificationWrapper<?>> filterableNotifications, @Nullable FrequencyType frequency) {
        SetMap<NotificationFilterMapModel, FilterableNotificationWrapper<?>> groupedFilterableNotifications = SetMap.createDefault();

        for (FilterableNotificationWrapper filterableNotificationWrapper : filterableNotifications) {
            List<FilteredDistributionJobModel> filteredDistributionJobModels = retrieveMatchingJobs(filterableNotificationWrapper, frequency);
        }
        return null;
    }

    private List<FilteredDistributionJobModel> retrieveMatchingJobs(FilterableNotificationWrapper filterableNotificationWrapper, FrequencyType frequencyType) {
        FilteredDistributionJobRequestModel filteredDistributionJobRequestModel = new FilteredDistributionJobRequestModel(
            frequencyType,
            EnumUtils.getEnum(NotificationType.class, filterableNotificationWrapper.extractNotificationType()),
            filterableNotificationWrapper.getProjectName(),
            filterableNotificationWrapper.getVulnerabilitySeverities(),
            filterableNotificationWrapper.getPolicyNames()
        );
        return jobAccessor.getMatchingEnabledJobs(filteredDistributionJobRequestModel);
    }

    private NotificationFilterMapModel createFilterMapModel() {
        return null;
    }

}
