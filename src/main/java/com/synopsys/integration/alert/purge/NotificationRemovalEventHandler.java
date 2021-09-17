/*
 * blackduck-alert
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.purge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.api.event.AlertEventHandler;
import com.synopsys.integration.alert.api.event.NotificationRemovalEvent;
import com.synopsys.integration.alert.common.persistence.accessor.NotificationAccessor;

@Component
public class NotificationRemovalEventHandler implements AlertEventHandler<NotificationRemovalEvent> {
    private static final int PAGE_SIZE = 10000;
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
        int totalRemoved = 0;
        while (notificationAccessor.existsNotificationsToRemove()) {
            totalRemoved += notificationAccessor.deleteNotificationsForRemoval(PAGE_SIZE);
        }
        logger.info("Purge event {}: Removed {} notifications", event.getEventId(), totalRemoved);
        logger.info("Finished notification removal event {}.", event.getEventId());
        notificationAccessor.getFirstPageOfNotificationsToRemove(PAGE_SIZE);
    }
}
