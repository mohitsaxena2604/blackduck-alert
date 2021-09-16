/*
 * blackduck-alert
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.purge;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.api.event.AlertEventHandler;
import com.synopsys.integration.alert.api.event.NotificationRemovalEvent;
import com.synopsys.integration.alert.common.persistence.accessor.NotificationAccessor;
import com.synopsys.integration.alert.common.rest.model.AlertNotificationModel;
import com.synopsys.integration.alert.common.rest.model.AlertPagedModel;

@Component
public class NotificationRemovalEventHandler implements AlertEventHandler<NotificationRemovalEvent> {
    private static final int PAGE_SIZE = 1000;
    private Logger logger = LoggerFactory.getLogger(getClass());

    private NotificationAccessor notificationAccessor;

    @Autowired
    public NotificationRemovalEventHandler(NotificationAccessor notificationAccessor) {
        this.notificationAccessor = notificationAccessor;
    }

    @Override
    public void handle(NotificationRemovalEvent event) {
        logger.debug("Event {}", event);
        logger.info("Start notification removal event {}.", event.getEventId());

        int numPages = 0;
        int totalRemoved = 0;
        AlertPagedModel<AlertNotificationModel> pageOfAlertNotificationModels;
        while (notificationAccessor.existsNotificationsToRemove()) {
            pageOfAlertNotificationModels = notificationAccessor.getFirstPageOfNotificationsToRemove(PAGE_SIZE);
            List<AlertNotificationModel> notifications = pageOfAlertNotificationModels.getModels();
            logger.info("Starting to remove {} notifications.", notifications.size());
            numPages++;
            totalRemoved += notificationAccessor.deleteNotifications(notifications);
            logger.trace("Removal Page: {}. New pages found: {}",
                numPages,
                pageOfAlertNotificationModels.getTotalPages());
        }
        logger.info("Purge event {}: Removed {} notifications", event.getEventId(), totalRemoved);
        logger.info("Finished notification removal event {}.", event.getEventId());
        notificationAccessor.getFirstPageOfNotificationsToRemove(PAGE_SIZE);
    }
}
