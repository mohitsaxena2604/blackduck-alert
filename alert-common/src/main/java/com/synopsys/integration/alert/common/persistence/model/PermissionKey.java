package com.synopsys.integration.alert.common.persistence.model;

import com.synopsys.integration.alert.common.rest.model.AlertSerializableModel;

public class PermissionKey extends AlertSerializableModel {
    private final String context;
    private final String descriptorName;

    public PermissionKey(String context, String descriptorName) {
        this.context = context;
        this.descriptorName = descriptorName;
    }

    public String getContext() {
        return context;
    }

    public String getDescriptorName() {
        return descriptorName;
    }

}
