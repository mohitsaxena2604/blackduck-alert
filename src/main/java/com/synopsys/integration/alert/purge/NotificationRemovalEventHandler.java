/*
 * blackduck-alert
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.purge;

import java.time.Duration;
import java.time.OffsetDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.api.event.AlertEventHandler;
import com.synopsys.integration.alert.api.event.NotificationRemovalEvent;
import com.synopsys.integration.alert.common.persistence.accessor.NotificationAccessor;

@Component
public class NotificationRemovalEventHandler implements AlertEventHandler<NotificationRemovalEvent> {
    private static final int PAGE_SIZE = 50000;
    private Logger logger = LoggerFactory.getLogger(getClass());

    private NotificationAccessor notificationAccessor;

    @Autowired
    public NotificationRemovalEventHandler(NotificationAccessor notificationAccessor) {
        this.notificationAccessor = notificationAccessor;
    }

    @Override
    public void handle(NotificationRemovalEvent event) {
        OffsetDateTime start = OffsetDateTime.now();
        logger.debug("Event {}", event);
        logger.info("Start notification removal event {}.", event.getEventId());
        //TODO have this post another event to keep removing batches.
        while (notificationAccessor.existsNotificationsToRemove()) {
            notificationAccessor.deleteNotificationsForRemoval(PAGE_SIZE);
        }
        OffsetDateTime end = OffsetDateTime.now();
        Duration duration = Duration.between(start, end);
        logger.info("Notification removal event took {}", duration.toString());
        logger.info("Finished notification removal event {}.", event.getEventId());
    }
}
