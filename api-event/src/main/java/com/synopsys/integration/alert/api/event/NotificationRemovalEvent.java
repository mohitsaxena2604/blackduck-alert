/*
 * api-event
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.api.event;

public class NotificationRemovalEvent extends AlertEvent {
    public static final String NOTIFICATION_PURGE_EVENT_TYPE = "notification_purge_event";

    public NotificationRemovalEvent() {
        super(NOTIFICATION_PURGE_EVENT_TYPE);
    }

}
