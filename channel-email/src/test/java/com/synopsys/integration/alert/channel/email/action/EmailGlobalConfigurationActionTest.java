package com.synopsys.integration.alert.channel.email.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import com.synopsys.integration.alert.api.common.model.exception.AlertConfigurationException;
import com.synopsys.integration.alert.channel.email.database.accessor.EmailGlobalConfigAccessor;
import com.synopsys.integration.alert.channel.email.validator.EmailGlobalConfigurationValidator;
import com.synopsys.integration.alert.common.action.ActionResponse;
import com.synopsys.integration.alert.common.enumeration.ConfigContextEnum;
import com.synopsys.integration.alert.common.persistence.model.PermissionKey;
import com.synopsys.integration.alert.common.persistence.model.PermissionMatrixModel;
import com.synopsys.integration.alert.common.rest.AlertRestConstants;
import com.synopsys.integration.alert.common.security.authorization.AuthorizationManager;
import com.synopsys.integration.alert.descriptor.api.model.ChannelKeys;
import com.synopsys.integration.alert.descriptor.api.model.DescriptorKey;
import com.synopsys.integration.alert.service.email.model.EmailGlobalConfigModel;
import com.synopsys.integration.alert.test.common.AuthenticationTestUtils;

public class EmailGlobalConfigurationActionTest {
    @Test
    public void testGetOne() {
        AuthenticationTestUtils authenticationTestUtils = new AuthenticationTestUtils();
        DescriptorKey descriptorKey = ChannelKeys.EMAIL;
        PermissionKey permissionKey = new PermissionKey(ConfigContextEnum.GLOBAL.name(), descriptorKey.getUniversalKey());
        Map<PermissionKey, Integer> permissions = Map.of(permissionKey, AuthenticationTestUtils.FULL_PERMISSIONS);
        AuthorizationManager authorizationManager = authenticationTestUtils.createAuthorizationManagerWithCurrentUserSet("admin", "admin", () -> new PermissionMatrixModel(permissions));
        EmailGlobalConfigurationValidator validator = new EmailGlobalConfigurationValidator();
        EmailGlobalConfigAccessor emailGlobalConfigAccessor = Mockito.mock(EmailGlobalConfigAccessor.class);
        EmailGlobalConfigModel model = new EmailGlobalConfigModel();
        model.setSmtpHost("host");
        model.setSmtpFrom("from");
        model.setSmtpAuth(true);
        model.setSmtpUsername("user");
        model.setSmtpPassword("password");
        Mockito.when(emailGlobalConfigAccessor.getConfiguration()).thenReturn(Optional.of(model));

        EmailGlobalCrudActions configActions = new EmailGlobalCrudActions(authorizationManager, emailGlobalConfigAccessor, validator);
        ActionResponse<EmailGlobalConfigModel> response = configActions.getOne();
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertTrue(response.hasContent());
        assertEquals(model.obfuscate(), response.getContent().get());
    }

    @Test
    public void testCreate() throws AlertConfigurationException {
        AuthenticationTestUtils authenticationTestUtils = new AuthenticationTestUtils();
        DescriptorKey descriptorKey = ChannelKeys.EMAIL;
        PermissionKey permissionKey = new PermissionKey(ConfigContextEnum.GLOBAL.name(), descriptorKey.getUniversalKey());
        Map<PermissionKey, Integer> permissions = Map.of(permissionKey, AuthenticationTestUtils.FULL_PERMISSIONS);
        AuthorizationManager authorizationManager = authenticationTestUtils.createAuthorizationManagerWithCurrentUserSet("admin", "admin", () -> new PermissionMatrixModel(permissions));
        EmailGlobalConfigurationValidator validator = new EmailGlobalConfigurationValidator();
        EmailGlobalConfigAccessor emailGlobalConfigAccessor = Mockito.mock(EmailGlobalConfigAccessor.class);
        EmailGlobalConfigModel model = new EmailGlobalConfigModel();
        model.setName(AlertRestConstants.DEFAULT_CONFIGURATION_NAME);
        model.setSmtpHost("host");
        model.setSmtpFrom("from");
        model.setSmtpAuth(true);
        model.setSmtpUsername("user");
        model.setSmtpPassword("password");
        Mockito.when(emailGlobalConfigAccessor.createConfiguration(Mockito.eq(model))).thenReturn(model);

        EmailGlobalCrudActions configActions = new EmailGlobalCrudActions(authorizationManager, emailGlobalConfigAccessor, validator);
        ActionResponse<EmailGlobalConfigModel> response = configActions.create(model);
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertTrue(response.hasContent());
        assertEquals(model.obfuscate(), response.getContent().get());
    }

    @Test
    public void testUpdate() throws AlertConfigurationException {
        AuthenticationTestUtils authenticationTestUtils = new AuthenticationTestUtils();
        DescriptorKey descriptorKey = ChannelKeys.EMAIL;
        PermissionKey permissionKey = new PermissionKey(ConfigContextEnum.GLOBAL.name(), descriptorKey.getUniversalKey());
        Map<PermissionKey, Integer> permissions = Map.of(permissionKey, AuthenticationTestUtils.FULL_PERMISSIONS);
        AuthorizationManager authorizationManager = authenticationTestUtils.createAuthorizationManagerWithCurrentUserSet("admin", "admin", () -> new PermissionMatrixModel(permissions));
        EmailGlobalConfigurationValidator validator = new EmailGlobalConfigurationValidator();
        EmailGlobalConfigAccessor emailGlobalConfigAccessor = Mockito.mock(EmailGlobalConfigAccessor.class);
        EmailGlobalConfigModel model = new EmailGlobalConfigModel();
        model.setName(AlertRestConstants.DEFAULT_CONFIGURATION_NAME);
        model.setSmtpHost("host");
        model.setSmtpFrom("from");
        model.setSmtpAuth(true);
        model.setSmtpUsername("user");
        model.setSmtpPassword("password");
        Mockito.when(emailGlobalConfigAccessor.getConfiguration()).thenReturn(Optional.of(model));
        Mockito.when(emailGlobalConfigAccessor.updateConfiguration(Mockito.eq(model))).thenReturn(model);
        Mockito.when(emailGlobalConfigAccessor.doesConfigurationExist()).thenReturn(true);

        EmailGlobalCrudActions configActions = new EmailGlobalCrudActions(authorizationManager, emailGlobalConfigAccessor, validator);
        ActionResponse<EmailGlobalConfigModel> response = configActions.update(model);
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertTrue(response.hasContent());
        assertEquals(model.obfuscate(), response.getContent().get());
    }

    @Test
    public void testDelete() {
        AuthenticationTestUtils authenticationTestUtils = new AuthenticationTestUtils();
        DescriptorKey descriptorKey = ChannelKeys.EMAIL;
        PermissionKey permissionKey = new PermissionKey(ConfigContextEnum.GLOBAL.name(), descriptorKey.getUniversalKey());
        Map<PermissionKey, Integer> permissions = Map.of(permissionKey, AuthenticationTestUtils.FULL_PERMISSIONS);
        AuthorizationManager authorizationManager = authenticationTestUtils.createAuthorizationManagerWithCurrentUserSet("admin", "admin", () -> new PermissionMatrixModel(permissions));
        EmailGlobalConfigurationValidator validator = new EmailGlobalConfigurationValidator();
        EmailGlobalConfigAccessor emailGlobalConfigAccessor = Mockito.mock(EmailGlobalConfigAccessor.class);
        EmailGlobalConfigModel model = new EmailGlobalConfigModel();
        model.setSmtpHost("host");
        model.setSmtpFrom("from");
        model.setSmtpAuth(true);
        model.setSmtpUsername("user");
        model.setSmtpPassword("password");
        Mockito.when(emailGlobalConfigAccessor.getConfiguration()).thenReturn(Optional.of(model));
        Mockito.when(emailGlobalConfigAccessor.doesConfigurationExist()).thenReturn(true);

        EmailGlobalCrudActions configActions = new EmailGlobalCrudActions(authorizationManager, emailGlobalConfigAccessor, validator);
        ActionResponse<EmailGlobalConfigModel> response = configActions.delete();
        assertEquals(HttpStatus.NO_CONTENT, response.getHttpStatus());
    }

    @Test
    public void testGetOneForbidden() {
        AuthenticationTestUtils authenticationTestUtils = new AuthenticationTestUtils();
        DescriptorKey descriptorKey = ChannelKeys.EMAIL;
        PermissionKey permissionKey = new PermissionKey(ConfigContextEnum.GLOBAL.name(), descriptorKey.getUniversalKey());
        Map<PermissionKey, Integer> permissions = Map.of(permissionKey, AuthenticationTestUtils.NO_PERMISSIONS);
        AuthorizationManager authorizationManager = authenticationTestUtils.createAuthorizationManagerWithCurrentUserSet("admin", "admin", () -> new PermissionMatrixModel(permissions));
        EmailGlobalConfigurationValidator validator = new EmailGlobalConfigurationValidator();
        EmailGlobalConfigAccessor emailGlobalConfigAccessor = Mockito.mock(EmailGlobalConfigAccessor.class);

        EmailGlobalCrudActions configActions = new EmailGlobalCrudActions(authorizationManager, emailGlobalConfigAccessor, validator);
        ActionResponse<EmailGlobalConfigModel> response = configActions.getOne();
        assertEquals(HttpStatus.FORBIDDEN, response.getHttpStatus());
    }

    @Test
    public void testCreateForbidden() {
        AuthenticationTestUtils authenticationTestUtils = new AuthenticationTestUtils();
        DescriptorKey descriptorKey = ChannelKeys.EMAIL;
        PermissionKey permissionKey = new PermissionKey(ConfigContextEnum.GLOBAL.name(), descriptorKey.getUniversalKey());
        Map<PermissionKey, Integer> permissions = Map.of(permissionKey, AuthenticationTestUtils.NO_PERMISSIONS);
        AuthorizationManager authorizationManager = authenticationTestUtils.createAuthorizationManagerWithCurrentUserSet("admin", "admin", () -> new PermissionMatrixModel(permissions));
        EmailGlobalConfigurationValidator validator = new EmailGlobalConfigurationValidator();
        EmailGlobalConfigAccessor emailGlobalConfigAccessor = Mockito.mock(EmailGlobalConfigAccessor.class);
        EmailGlobalConfigModel model = new EmailGlobalConfigModel();
        model.setSmtpHost("host");
        model.setSmtpFrom("from");
        model.setSmtpAuth(true);
        model.setSmtpUsername("user");
        model.setSmtpPassword("password");

        EmailGlobalCrudActions configActions = new EmailGlobalCrudActions(authorizationManager, emailGlobalConfigAccessor, validator);
        ActionResponse<EmailGlobalConfigModel> response = configActions.create(model);
        assertEquals(HttpStatus.FORBIDDEN, response.getHttpStatus());
    }

    @Test
    public void testUpdateForbidden() {
        AuthenticationTestUtils authenticationTestUtils = new AuthenticationTestUtils();
        DescriptorKey descriptorKey = ChannelKeys.EMAIL;
        PermissionKey permissionKey = new PermissionKey(ConfigContextEnum.GLOBAL.name(), descriptorKey.getUniversalKey());
        Map<PermissionKey, Integer> permissions = Map.of(permissionKey, AuthenticationTestUtils.NO_PERMISSIONS);
        AuthorizationManager authorizationManager = authenticationTestUtils.createAuthorizationManagerWithCurrentUserSet("admin", "admin", () -> new PermissionMatrixModel(permissions));
        UUID configId = UUID.randomUUID();
        EmailGlobalConfigurationValidator validator = new EmailGlobalConfigurationValidator();
        EmailGlobalConfigAccessor emailGlobalConfigAccessor = Mockito.mock(EmailGlobalConfigAccessor.class);
        EmailGlobalConfigModel model = new EmailGlobalConfigModel();
        model.setSmtpHost("host");
        model.setSmtpFrom("from");
        model.setSmtpAuth(true);
        model.setSmtpUsername("user");
        model.setSmtpPassword("password");

        EmailGlobalCrudActions configActions = new EmailGlobalCrudActions(authorizationManager, emailGlobalConfigAccessor, validator);
        ActionResponse<EmailGlobalConfigModel> response = configActions.update(model);
        assertEquals(HttpStatus.FORBIDDEN, response.getHttpStatus());
    }

    @Test
    public void testUpdateNotFound() {
        AuthenticationTestUtils authenticationTestUtils = new AuthenticationTestUtils();
        DescriptorKey descriptorKey = ChannelKeys.EMAIL;
        PermissionKey permissionKey = new PermissionKey(ConfigContextEnum.GLOBAL.name(), descriptorKey.getUniversalKey());
        Map<PermissionKey, Integer> permissions = Map.of(permissionKey, AuthenticationTestUtils.FULL_PERMISSIONS);
        AuthorizationManager authorizationManager = authenticationTestUtils.createAuthorizationManagerWithCurrentUserSet("admin", "admin", () -> new PermissionMatrixModel(permissions));
        UUID configId = UUID.randomUUID();
        EmailGlobalConfigurationValidator validator = new EmailGlobalConfigurationValidator();
        EmailGlobalConfigAccessor emailGlobalConfigAccessor = Mockito.mock(EmailGlobalConfigAccessor.class);
        Mockito.when(emailGlobalConfigAccessor.getConfiguration()).thenReturn(Optional.empty());

        EmailGlobalConfigModel model = new EmailGlobalConfigModel();
        model.setSmtpHost("host");
        model.setSmtpFrom("from");
        model.setSmtpAuth(true);
        model.setSmtpUsername("user");
        model.setSmtpPassword("password");

        EmailGlobalCrudActions configActions = new EmailGlobalCrudActions(authorizationManager, emailGlobalConfigAccessor, validator);
        ActionResponse<EmailGlobalConfigModel> response = configActions.update(model);
        assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
    }

    @Test
    public void testDeleteForbidden() {
        AuthenticationTestUtils authenticationTestUtils = new AuthenticationTestUtils();
        DescriptorKey descriptorKey = ChannelKeys.EMAIL;
        PermissionKey permissionKey = new PermissionKey(ConfigContextEnum.GLOBAL.name(), descriptorKey.getUniversalKey());
        Map<PermissionKey, Integer> permissions = Map.of(permissionKey, AuthenticationTestUtils.NO_PERMISSIONS);
        AuthorizationManager authorizationManager = authenticationTestUtils.createAuthorizationManagerWithCurrentUserSet("admin", "admin", () -> new PermissionMatrixModel(permissions));
        UUID configId = UUID.randomUUID();
        EmailGlobalConfigurationValidator validator = new EmailGlobalConfigurationValidator();
        EmailGlobalConfigAccessor emailGlobalConfigAccessor = Mockito.mock(EmailGlobalConfigAccessor.class);

        EmailGlobalCrudActions configActions = new EmailGlobalCrudActions(authorizationManager, emailGlobalConfigAccessor, validator);
        ActionResponse<EmailGlobalConfigModel> response = configActions.delete();
        assertEquals(HttpStatus.FORBIDDEN, response.getHttpStatus());
    }

    @Test
    public void testDeleteNotFound() {
        AuthenticationTestUtils authenticationTestUtils = new AuthenticationTestUtils();
        DescriptorKey descriptorKey = ChannelKeys.EMAIL;
        PermissionKey permissionKey = new PermissionKey(ConfigContextEnum.GLOBAL.name(), descriptorKey.getUniversalKey());
        Map<PermissionKey, Integer> permissions = Map.of(permissionKey, AuthenticationTestUtils.FULL_PERMISSIONS);
        AuthorizationManager authorizationManager = authenticationTestUtils.createAuthorizationManagerWithCurrentUserSet("admin", "admin", () -> new PermissionMatrixModel(permissions));
        EmailGlobalConfigurationValidator validator = new EmailGlobalConfigurationValidator();
        EmailGlobalConfigAccessor emailGlobalConfigAccessor = Mockito.mock(EmailGlobalConfigAccessor.class);
        Mockito.when(emailGlobalConfigAccessor.getConfiguration()).thenReturn(Optional.empty());

        EmailGlobalCrudActions configActions = new EmailGlobalCrudActions(authorizationManager, emailGlobalConfigAccessor, validator);
        ActionResponse<EmailGlobalConfigModel> response = configActions.delete();
        assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
    }

}
