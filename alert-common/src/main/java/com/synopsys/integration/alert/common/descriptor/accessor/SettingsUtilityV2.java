package com.synopsys.integration.alert.common.descriptor.accessor;

import java.util.Optional;

import com.synopsys.integration.alert.common.rest.model.SettingsProxyModel;
import com.synopsys.integration.alert.descriptor.api.model.DescriptorKey;

public interface SettingsUtilityV2 {
    //TODO: This is used in AlertStartupInitializer::initializeConfigs and is set to be Deprecated
    DescriptorKey getKey();

    Optional<SettingsProxyModel> getConfiguration();
}
