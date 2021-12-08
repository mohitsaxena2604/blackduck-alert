package com.synopsys.integration.alert.component.settings;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.common.descriptor.accessor.SettingsUtilityV2;
import com.synopsys.integration.alert.common.rest.model.AlertPagedModel;
import com.synopsys.integration.alert.common.rest.model.SettingsProxyModel;
import com.synopsys.integration.alert.component.settings.descriptor.SettingsDescriptorKey;
import com.synopsys.integration.alert.component.settings.proxy.database.accessor.SettingsProxyConfigAccessor;
import com.synopsys.integration.alert.descriptor.api.model.DescriptorKey;

@Component
public class DefaultSettingsUtilityV2 implements SettingsUtilityV2 {
    SettingsProxyConfigAccessor settingsProxyConfigAccessor;
    SettingsDescriptorKey settingsDescriptorKey;

    @Autowired
    public DefaultSettingsUtilityV2(SettingsProxyConfigAccessor settingsProxyConfigAccessor, SettingsDescriptorKey settingsDescriptorKey) {
        this.settingsProxyConfigAccessor = settingsProxyConfigAccessor;
        this.settingsDescriptorKey = settingsDescriptorKey;
    }

    @Override
    public DescriptorKey getKey() {
        return settingsDescriptorKey;
    }

    @Override
    public Optional<SettingsProxyModel> getConfiguration() {
        //TODO: Need a better approach to getting configuration than findFirst
        AlertPagedModel<SettingsProxyModel> pagedModel = settingsProxyConfigAccessor.getConfigurationPage(0, 10);
        return pagedModel.getModels().stream().findFirst();
    }

}
