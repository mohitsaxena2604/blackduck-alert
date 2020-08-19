package com.synopsys.integration.alert.web.controller;

import static org.junit.Assert.assertEquals;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.gson.Gson;
import com.synopsys.integration.alert.common.ContentConverter;
import com.synopsys.integration.alert.common.descriptor.config.ui.DescriptorMetadata;
import com.synopsys.integration.alert.common.rest.ResponseFactory;
import com.synopsys.integration.alert.web.api.about.AboutActions;
import com.synopsys.integration.alert.web.api.about.AboutController;
import com.synopsys.integration.alert.web.api.about.AboutModel;

public class AboutControllerTest {
    private final Gson gson = new Gson();
    ContentConverter contentConverter = new ContentConverter(gson, new DefaultConversionService());

    @Test
    public void testController() {
        String version = "1.2.3";
        String created = "date";
        String description = "description";
        String aUrl = "https://www.google.com";
        boolean initialized = true;
        String startupTime = "startup time is now";
        DescriptorMetadata providerMetadata = Mockito.mock(DescriptorMetadata.class);
        DescriptorMetadata channelMetadata = Mockito.mock(DescriptorMetadata.class);
        Set<DescriptorMetadata> providers = Set.of(providerMetadata);
        Set<DescriptorMetadata> channels = Set.of(channelMetadata);

        ResponseFactory responseFactory = new ResponseFactory();
        AboutModel model = new AboutModel(version, created, description, aUrl, aUrl, initialized, startupTime, providers, channels);
        AboutActions aboutActions = Mockito.mock(AboutActions.class);

        Mockito.when(aboutActions.getAboutModel()).thenReturn(Optional.of(model));
        AboutController controller = new AboutController(aboutActions);
        ResponseEntity<String> response = controller.getAbout();

        ResponseEntity<String> expectedResponse = responseFactory.createOkContentResponse(contentConverter.getJsonString(model));
        assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());
        assertEquals(expectedResponse.getBody(), response.getBody());
    }

    @Test
    public void testGetAboutData() {
        String version = "1.2.3";
        String created = "date";
        String description = "description";
        String aUrl = "https://www.google.com";
        boolean initialized = true;
        String startupTime = "startup time is now";
        DescriptorMetadata providerMetadata = Mockito.mock(DescriptorMetadata.class);
        DescriptorMetadata channelMetadata = Mockito.mock(DescriptorMetadata.class);
        Set<DescriptorMetadata> providers = Set.of(providerMetadata);
        Set<DescriptorMetadata> channels = Set.of(channelMetadata);

        Gson gson = new Gson();
        ContentConverter contentConverter = new ContentConverter(gson, new DefaultConversionService());
        ResponseFactory responseFactory = new ResponseFactory();

        AboutModel model = new AboutModel(version, created, description, aUrl, aUrl, initialized, startupTime, providers, channels);
        AboutActions aboutActions = Mockito.mock(AboutActions.class);
        AboutController aboutController = new AboutController(aboutActions);

        Mockito.when(aboutActions.getAboutModel()).thenReturn(Optional.of(model));

        ResponseEntity<String> response = aboutController.getAbout();
        ResponseEntity<String> expectedResponse = responseFactory.createOkContentResponse(contentConverter.getJsonString(model));
        assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());
        assertEquals(expectedResponse.getBody(), response.getBody());
    }

    @Test
    public void testGetAboutDataNotPresent() {
        Gson gson = new Gson();
        ContentConverter contentConverter = new ContentConverter(gson, new DefaultConversionService());

        ResponseFactory responseFactory = new ResponseFactory();
        AboutActions aboutActions = Mockito.mock(AboutActions.class);
        AboutController aboutController = new AboutController(aboutActions);

        Mockito.when(aboutActions.getAboutModel()).thenReturn(Optional.empty());

        ResponseEntity<String> response = aboutController.getAbout();
        ResponseEntity<String> expectedResponse = responseFactory.createMessageResponse(HttpStatus.NOT_FOUND, AboutController.ERROR_ABOUT_MODEL_NOT_FOUND);
        assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());
        assertEquals(expectedResponse.getBody(), response.getBody());
    }

}
