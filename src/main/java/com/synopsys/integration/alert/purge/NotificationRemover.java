/*
 * blackduck-alert
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.purge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.synopsys.integration.alert.api.event.AlertMessageListener;
import com.synopsys.integration.alert.api.event.NotificationPurgeEvent;

@Component(value = NotificationRemover.COMPONENT_NAME)
public class NotificationRemover extends AlertMessageListener<NotificationPurgeEvent> {
    public static final String COMPONENT_NAME = "notification_remover";

    @Autowired
    public NotificationRemover(Gson gson, NotificationRemovalEventHandler eventHandler) {
        super(gson, NotificationPurgeEvent.NOTIFICATION_PURGE_EVENT_TYPE, NotificationPurgeEvent.class, eventHandler);
    }

}
