/**
 * blackduck-alert
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.alert.provider.blackduck.collector.builder.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.common.enumeration.ComponentItemPriority;
import com.synopsys.integration.alert.common.enumeration.ItemOperation;
import com.synopsys.integration.alert.common.exception.AlertException;
import com.synopsys.integration.alert.common.message.model.ComponentItem;
import com.synopsys.integration.alert.common.message.model.LinkableItem;
import com.synopsys.integration.alert.common.message.model.ProviderMessageContent;
import com.synopsys.integration.alert.common.persistence.model.ConfigurationJobModel;
import com.synopsys.integration.alert.provider.blackduck.collector.builder.BlackDuckMessageBuilder;
import com.synopsys.integration.alert.provider.blackduck.collector.builder.MessageBuilderConstants;
import com.synopsys.integration.alert.provider.blackduck.collector.builder.util.ComponentBuilderUtil;
import com.synopsys.integration.alert.provider.blackduck.collector.builder.util.PolicyPriorityUtil;
import com.synopsys.integration.alert.provider.blackduck.collector.util.BlackDuckResponseCache;
import com.synopsys.integration.blackduck.api.generated.enumeration.NotificationType;
import com.synopsys.integration.blackduck.api.generated.view.VersionBomComponentView;
import com.synopsys.integration.blackduck.api.manual.component.PolicyInfo;
import com.synopsys.integration.blackduck.api.manual.component.PolicyOverrideNotificationContent;
import com.synopsys.integration.blackduck.api.manual.view.PolicyOverrideNotificationView;
import com.synopsys.integration.blackduck.service.BlackDuckService;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.ComponentService;
import com.synopsys.integration.blackduck.service.bucket.BlackDuckBucket;
import com.synopsys.integration.blackduck.service.bucket.BlackDuckBucketService;

@Component
public class PolicyOverrideMessageBuilder implements BlackDuckMessageBuilder<PolicyOverrideNotificationView> {
    private final Logger logger = LoggerFactory.getLogger(PolicyClearedMessageBuilder.class);
    private PolicyPriorityUtil policyPriorityUtil;
    private ComponentBuilderUtil componentBuilderUtil;

    @Autowired
    public PolicyOverrideMessageBuilder(PolicyPriorityUtil policyPriorityUtil, ComponentBuilderUtil componentBuilderUtil) {
        this.policyPriorityUtil = policyPriorityUtil;
        this.componentBuilderUtil = componentBuilderUtil;
    }

    @Override
    public String getNotificationType() {
        return NotificationType.POLICY_OVERRIDE.name();
    }

    @Override
    public List<ProviderMessageContent> buildMessageContents(Long notificationId, Date providerCreationDate, ConfigurationJobModel job, PolicyOverrideNotificationView notificationView,
        BlackDuckBucket blackDuckBucket, BlackDuckServicesFactory blackDuckServicesFactory) {
        long timeout = blackDuckServicesFactory.getBlackDuckHttpClient().getTimeoutInSeconds();
        BlackDuckBucketService bucketService = blackDuckServicesFactory.createBlackDuckBucketService();
        BlackDuckResponseCache responseCache = new BlackDuckResponseCache(bucketService, blackDuckBucket, timeout);
        BlackDuckService blackDuckService = blackDuckServicesFactory.createBlackDuckService();
        ComponentService componentService = blackDuckServicesFactory.createComponentService();
        PolicyOverrideNotificationContent overrideContent = notificationView.getContent();
        ItemOperation operation = ItemOperation.DELETE;
        try {
            ProviderMessageContent.Builder projectVersionMessageBuilder = new ProviderMessageContent.Builder()
                                                                              .applyProvider(getProviderName(), blackDuckServicesFactory.getBlackDuckHttpClient().getBaseUrl())
                                                                              .applyTopic(MessageBuilderConstants.LABEL_PROJECT_NAME, overrideContent.getProjectName())
                                                                              .applySubTopic(MessageBuilderConstants.LABEL_PROJECT_VERSION_NAME, overrideContent.getProjectVersionName(), overrideContent.getProjectVersion())
                                                                              .applyProviderCreationTime(providerCreationDate);

            List<PolicyInfo> policies = overrideContent.getPolicyInfos();
            List<ComponentItem> items = retrievePolicyItems(responseCache, blackDuckService, componentService, overrideContent, policies, notificationId, operation, overrideContent.getProjectVersion());
            projectVersionMessageBuilder.applyAllComponentItems(items);
            return List.of(projectVersionMessageBuilder.build());
        } catch (AlertException ex) {
            logger.error("Error creating policy override message.", ex);
        }

        return List.of();
    }

    private List<ComponentItem> retrievePolicyItems(BlackDuckResponseCache blackDuckResponseCache, BlackDuckService blackDuckService, ComponentService componentService, PolicyOverrideNotificationContent overrideContent,
        Collection<PolicyInfo> policies, Long notificationId, ItemOperation operation, String projectVersionUrl) {
        List<ComponentItem> componentItems = new LinkedList<>();
        for (PolicyInfo policyInfo : policies) {
            ComponentItemPriority priority = policyPriorityUtil.getPriorityFromSeverity(policyInfo.getSeverity());

            String bomComponentUrl = overrideContent.getBomComponent();

            Optional<VersionBomComponentView> optionalBomComponent = blackDuckResponseCache.getBomComponentView(bomComponentUrl);

            List<LinkableItem> policyAttributes = new ArrayList<>();
            LinkableItem policyNameItem = componentBuilderUtil.createPolicyNameItem(policyInfo);
            LinkableItem nullablePolicySeverityItem = componentBuilderUtil.createPolicySeverityItem(policyInfo).orElse(null);
            optionalBomComponent.ifPresent(bomComponent -> {
                policyAttributes.addAll(componentBuilderUtil.getLicenseLinkableItems(bomComponent));
                policyAttributes.addAll(componentBuilderUtil.getUsageLinkableItems(bomComponent));
            });

            String firstName = overrideContent.getFirstName();
            String lastName = overrideContent.getLastName();

            String overrideBy = String.format("%s %s", firstName, lastName);
            policyAttributes.add(new LinkableItem(MessageBuilderConstants.LABEL_POLICY_OVERRIDE_BY, overrideBy));

            String componentName = overrideContent.getComponentName();
            String componentVersionName = overrideContent.getComponentVersionName();

            try {
                ComponentItem.Builder builder = new ComponentItem.Builder()
                                                    .applyCategory(MessageBuilderConstants.CATEGORY_TYPE_POLICY)
                                                    .applyOperation(operation)
                                                    .applyPriority(priority)
                                                    .applyCategoryItem(policyNameItem)
                                                    .applyCategoryGroupingAttribute(nullablePolicySeverityItem)
                                                    .applyAllComponentAttributes(policyAttributes)
                                                    .applyNotificationId(notificationId);
                componentBuilderUtil.applyComponentInformation(builder, blackDuckResponseCache, componentName, componentVersionName, projectVersionUrl);
                componentItems.add(builder.build());
            } catch (Exception ex) {
                logger.info("Error building policy component for notification {}, operation {}, component {}, component version {}", notificationId, operation, componentName, componentVersionName);
                logger.error("Error building policy component cause ", ex);
            }
        }
        return componentItems;
    }

}
