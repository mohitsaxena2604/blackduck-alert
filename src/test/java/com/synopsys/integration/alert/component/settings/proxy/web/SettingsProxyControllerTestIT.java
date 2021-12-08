package com.synopsys.integration.alert.component.settings.proxy.web;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.google.common.reflect.TypeToken;

import com.google.gson.Gson;
import com.synopsys.integration.alert.common.rest.AlertRestConstants;
import com.synopsys.integration.alert.common.rest.model.SettingsProxyModel;
import com.synopsys.integration.alert.database.settings.proxy.NonProxyHostsConfigurationRepository;
import com.synopsys.integration.alert.database.settings.proxy.SettingsProxyConfigurationRepository;
import com.synopsys.integration.alert.util.AlertIntegrationTest;
import com.synopsys.integration.alert.util.AlertIntegrationTestConstants;

@Transactional
@AlertIntegrationTest
public class SettingsProxyControllerTestIT {
    private static final String HOST = "hostname";
    private static final Integer PORT = 12345;
    private static final String USERNAME = "userName";
    private static final String PASSWORD = "myPassword";

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Autowired
    protected WebApplicationContext webApplicationContext;
    @Autowired
    private Gson gson;
    @Autowired
    protected HttpSessionCsrfTokenRepository csrfTokenRepository;
    @Autowired
    private SettingsProxyConfigurationRepository settingsProxyConfigurationRepository;
    @Autowired
    private NonProxyHostsConfigurationRepository nonProxyHostsConfigurationRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @AfterEach
    public void cleanup() {
        settingsProxyConfigurationRepository.flush();
        nonProxyHostsConfigurationRepository.flush();
        settingsProxyConfigurationRepository.deleteAllInBatch();
        nonProxyHostsConfigurationRepository.deleteAllInBatch();
    }

    @Test
    @WithMockUser(roles = AlertIntegrationTestConstants.ROLE_ALERT_ADMIN)
    public void testCreate() throws Exception {
        SettingsProxyModel settingsProxyModel = createSettingsProxyModel();

        String url = AlertRestConstants.SETTINGS_PROXY_PATH;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(new URI(url))
                                                    .with(SecurityMockMvcRequestPostProcessors.user("admin").roles(AlertIntegrationTestConstants.ROLE_ALERT_ADMIN))
                                                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                                                    .content(gson.toJson(settingsProxyModel))
                                                    .contentType(contentType);
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @WithMockUser(roles = AlertIntegrationTestConstants.ROLE_ALERT_ADMIN)
    public void testGetOne() throws Exception {
        SettingsProxyModel settingsProxyModel = createDefaultSettingsProxyModel().orElseThrow(AssertionFailedError::new);

        String url = AlertRestConstants.SETTINGS_PROXY_PATH + String.format("/%s", settingsProxyModel.getId());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(new URI(url))
                                                    .with(SecurityMockMvcRequestPostProcessors.user("admin").roles(AlertIntegrationTestConstants.ROLE_ALERT_ADMIN))
                                                    .with(SecurityMockMvcRequestPostProcessors.csrf());
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(roles = AlertIntegrationTestConstants.ROLE_ALERT_ADMIN)
    public void getPageTest() throws Exception {
        int pageNumber = 0;
        int pageSize = 1;
        createDefaultSettingsProxyModel().orElseThrow(AssertionFailedError::new);
        createDefaultSettingsProxyModel().orElseThrow(AssertionFailedError::new);

        String url = AlertRestConstants.SETTINGS_PROXY_PATH + "?pageNumber=" + pageNumber + "&pageSize=" + pageSize;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(new URI(url))
                                                    .with(SecurityMockMvcRequestPostProcessors.user("admin").roles(AlertIntegrationTestConstants.ROLE_ALERT_ADMIN))
                                                    .with(SecurityMockMvcRequestPostProcessors.csrf());
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(roles = AlertIntegrationTestConstants.ROLE_ALERT_ADMIN)
    public void testUpdate() throws Exception {
        SettingsProxyModel defaultSettingsProxyModel = createDefaultSettingsProxyModel().orElseThrow(AssertionFailedError::new);
        SettingsProxyModel newSettingsProxyModel = new SettingsProxyModel();
        newSettingsProxyModel.setProxyHost("newHostname");
        newSettingsProxyModel.setProxyPort(678);

        String url = AlertRestConstants.SETTINGS_PROXY_PATH + String.format("/%s", defaultSettingsProxyModel.getId());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(new URI(url))
                                                    .with(SecurityMockMvcRequestPostProcessors.user("admin").roles(AlertIntegrationTestConstants.ROLE_ALERT_ADMIN))
                                                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                                                    .content(gson.toJson(newSettingsProxyModel))
                                                    .contentType(contentType);
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testDelete() throws Exception {
        SettingsProxyModel defaultSettingsProxyModel = createDefaultSettingsProxyModel().orElseThrow(AssertionFailedError::new);

        String url = AlertRestConstants.SETTINGS_PROXY_PATH + String.format("/%s", defaultSettingsProxyModel.getId());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(new URI(url))
                                                    .with(SecurityMockMvcRequestPostProcessors.user("admin").roles(AlertIntegrationTestConstants.ROLE_ALERT_ADMIN))
                                                    .with(SecurityMockMvcRequestPostProcessors.csrf());
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testValidate() throws Exception {
        SettingsProxyModel settingsProxyModel = createSettingsProxyModel();

        String url = AlertRestConstants.SETTINGS_PROXY_PATH + "/validate";
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(new URI(url))
                                                    .with(SecurityMockMvcRequestPostProcessors.user("admin").roles(AlertIntegrationTestConstants.ROLE_ALERT_ADMIN))
                                                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                                                    .content(gson.toJson(settingsProxyModel))
                                                    .contentType(contentType);
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    private SettingsProxyModel createSettingsProxyModel() {
        SettingsProxyModel settingsProxyModel = new SettingsProxyModel();
        settingsProxyModel.setProxyHost(HOST);
        settingsProxyModel.setProxyPort(PORT);
        settingsProxyModel.setProxyUsername(USERNAME);
        settingsProxyModel.setProxyPassword(PASSWORD);
        settingsProxyModel.setNonProxyHosts(List.of("hosts"));
        return settingsProxyModel;
    }

    private Optional<SettingsProxyModel> createDefaultSettingsProxyModel() throws Exception {
        SettingsProxyModel settingsProxyModel = createSettingsProxyModel();

        String url = AlertRestConstants.SETTINGS_PROXY_PATH;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(new URI(url))
                                                    .with(SecurityMockMvcRequestPostProcessors.user("admin").roles(AlertIntegrationTestConstants.ROLE_ALERT_ADMIN))
                                                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                                                    .content(gson.toJson(settingsProxyModel))
                                                    .contentType(contentType);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        TypeToken<SettingsProxyModel> settingsProxyModelType = new TypeToken<>() {};
        SettingsProxyModel newSettingsProxyModel = gson.fromJson(response, settingsProxyModelType.getType());
        return Optional.of(newSettingsProxyModel);
    }
}
