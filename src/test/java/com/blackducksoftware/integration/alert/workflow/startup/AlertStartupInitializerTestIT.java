package com.blackducksoftware.integration.alert.workflow.startup;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;

import com.blackducksoftware.integration.alert.channel.email.EmailDescriptor;
import com.blackducksoftware.integration.alert.channel.email.EmailGlobalContentConverter;
import com.blackducksoftware.integration.alert.channel.email.mock.MockEmailGlobalEntity;
import com.blackducksoftware.integration.alert.common.ContentConverter;
import com.blackducksoftware.integration.alert.common.descriptor.Descriptor;
import com.blackducksoftware.integration.alert.database.channel.email.EmailGlobalRepository;
import com.blackducksoftware.integration.alert.database.channel.email.EmailGlobalRepositoryAccessor;
import com.blackducksoftware.integration.alert.database.entity.EntityPropertyMapper;
import com.blackducksoftware.integration.alert.web.channel.model.EmailGlobalConfig;
import com.blackducksoftware.integration.alert.workflow.PropertyInitializer;
import com.google.gson.Gson;

public class AlertStartupInitializerTestIT {

    @Test
    public void testInitializeConfigs() throws Exception {
        final Environment environment = Mockito.mock(Environment.class);
        final ConversionService conversionService = new DefaultConversionService();
        final EmailGlobalRepository emailGlobalRepository = Mockito.mock(EmailGlobalRepository.class);
        final PropertyInitializer propertyInitializer = new PropertyInitializer();
        final ContentConverter contentConverter = new ContentConverter(new Gson(), new DefaultConversionService());
        final EmailGlobalContentConverter emailGlobalContentConverter = new EmailGlobalContentConverter(contentConverter);
        final EmailGlobalRepositoryAccessor emailGlobalRepositoryAccessor = new EmailGlobalRepositoryAccessor(emailGlobalRepository);
        final EntityPropertyMapper entityPropertyMapper = new EntityPropertyMapper();
        final List<Descriptor> descriptors = Arrays.asList(new EmailDescriptor(null, emailGlobalContentConverter, emailGlobalRepositoryAccessor, null, null, entityPropertyMapper));
        final AlertStartupInitializer initializer = new AlertStartupInitializer(propertyInitializer, descriptors, environment, conversionService);
        initializer.initializeConfigs();
        assertFalse(initializer.getAlertStartupProperties().isEmpty());
        assertFalse(initializer.getAlertPropertyNameSet().isEmpty());
    }

    @Test
    public void testInitializeConfigsEmptyInitializerList() throws Exception {
        final Environment environment = Mockito.mock(Environment.class);
        final ConversionService conversionService = new DefaultConversionService();
        final PropertyInitializer propertyInitializer = new PropertyInitializer();
        final List<Descriptor> descriptors = Arrays.asList();
        final AlertStartupInitializer initializer = new AlertStartupInitializer(propertyInitializer, descriptors, environment, conversionService);
        initializer.initializeConfigs();
        assertTrue(initializer.getAlertStartupProperties().isEmpty());
        assertTrue(initializer.getAlertPropertyNameSet().isEmpty());
    }

    @Test
    public void testSetRestModelValue() throws Exception {
        final Environment environment = Mockito.mock(Environment.class);
        final ConversionService conversionService = new DefaultConversionService();
        final EmailGlobalRepository emailGlobalRepository = Mockito.mock(EmailGlobalRepository.class);
        final PropertyInitializer propertyInitializer = new PropertyInitializer();
        final ContentConverter contentConverter = new ContentConverter(new Gson(), new DefaultConversionService());
        final EmailGlobalContentConverter emailGlobalContentConverter = new EmailGlobalContentConverter(contentConverter);
        final EmailGlobalRepositoryAccessor emailGlobalRepositoryAccessor = new EmailGlobalRepositoryAccessor(emailGlobalRepository);
        final EntityPropertyMapper entityPropertyMapper = new EntityPropertyMapper();
        final List<Descriptor> descriptors = Arrays.asList(new EmailDescriptor(null, emailGlobalContentConverter, emailGlobalRepositoryAccessor, null, null, entityPropertyMapper));
        final AlertStartupInitializer initializer = new AlertStartupInitializer(propertyInitializer, descriptors, environment, conversionService);
        initializer.initializeConfigs();
        final String value = "newValue";

        final EmailGlobalConfig globalRestModel = new EmailGlobalConfig();
        final AlertStartupProperty property = initializer.getAlertStartupProperties().get(0);
        initializer.setRestModelValue(value, globalRestModel, property);

        final Field declaredField = globalRestModel.getClass().getDeclaredField(property.getFieldName());
        final boolean accessible = declaredField.isAccessible();

        declaredField.setAccessible(true);
        final Object fieldValue = declaredField.get(globalRestModel);
        declaredField.setAccessible(accessible);
        assertEquals(value, fieldValue);
    }

    @Test
    public void testSetRestModelValueEmtpyValue() throws Exception {
        final Environment environment = Mockito.mock(Environment.class);
        final ConversionService conversionService = new DefaultConversionService();
        final EmailGlobalRepository emailGlobalRepository = Mockito.mock(EmailGlobalRepository.class);
        final PropertyInitializer propertyInitializer = new PropertyInitializer();
        final ContentConverter contentConverter = new ContentConverter(new Gson(), new DefaultConversionService());
        final EmailGlobalContentConverter emailGlobalContentConverter = new EmailGlobalContentConverter(contentConverter);
        final EmailGlobalRepositoryAccessor emailGlobalRepositoryAccessor = new EmailGlobalRepositoryAccessor(emailGlobalRepository);
        final EntityPropertyMapper entityPropertyMapper = new EntityPropertyMapper();
        final List<Descriptor> descriptors = Arrays.asList(new EmailDescriptor(null, emailGlobalContentConverter, emailGlobalRepositoryAccessor, null, null, entityPropertyMapper));
        final AlertStartupInitializer initializer = new AlertStartupInitializer(propertyInitializer, descriptors, environment, conversionService);
        initializer.initializeConfigs();

        final EmailGlobalConfig globalRestModel = new EmailGlobalConfig();
        final AlertStartupProperty property = initializer.getAlertStartupProperties().get(0);
        initializer.setRestModelValue(null, globalRestModel, property);

        final Field declaredField = globalRestModel.getClass().getDeclaredField(property.getFieldName());
        final boolean accessible = declaredField.isAccessible();

        declaredField.setAccessible(true);
        final Object fieldValue = declaredField.get(globalRestModel);
        declaredField.setAccessible(accessible);
        assertNull(fieldValue);
    }

    @Test
    public void testCanConvertFalse() throws Exception {
        final Environment environment = Mockito.mock(Environment.class);
        final ConversionService conversionService = Mockito.mock(ConversionService.class);
        Mockito.when(conversionService.canConvert(Mockito.any(Class.class), Mockito.any(Class.class))).thenReturn(false);
        final EmailGlobalRepository emailGlobalRepository = Mockito.mock(EmailGlobalRepository.class);
        final PropertyInitializer propertyInitializer = new PropertyInitializer();
        final ContentConverter contentConverter = new ContentConverter(new Gson(), new DefaultConversionService());
        final EmailGlobalContentConverter emailGlobalContentConverter = new EmailGlobalContentConverter(contentConverter);
        final EmailGlobalRepositoryAccessor emailGlobalRepositoryAccessor = new EmailGlobalRepositoryAccessor(emailGlobalRepository);
        final EntityPropertyMapper entityPropertyMapper = new EntityPropertyMapper();
        final List<Descriptor> descriptors = Arrays.asList(new EmailDescriptor(null, emailGlobalContentConverter, emailGlobalRepositoryAccessor, null, null, entityPropertyMapper));
        final AlertStartupInitializer initializer = new AlertStartupInitializer(propertyInitializer, descriptors, environment, conversionService);
        initializer.initializeConfigs();
        final EmailGlobalConfig globalRestModel = new EmailGlobalConfig();
        final AlertStartupProperty property = initializer.getAlertStartupProperties().get(0);
        initializer.setRestModelValue("a value that can't be converted", globalRestModel, property);

        final Field declaredField = globalRestModel.getClass().getDeclaredField(property.getFieldName());
        final boolean accessible = declaredField.isAccessible();

        declaredField.setAccessible(true);
        final Object fieldValue = declaredField.get(globalRestModel);
        declaredField.setAccessible(accessible);
        assertNull(fieldValue);
    }

    @Test
    public void testSystemPropertyExists() throws Exception {
        final Environment environment = Mockito.mock(Environment.class);
        final ConversionService conversionService = Mockito.mock(ConversionService.class);
        Mockito.when(conversionService.canConvert(Mockito.any(Class.class), Mockito.any(Class.class))).thenReturn(true);
        final EmailGlobalRepository emailGlobalRepository = Mockito.mock(EmailGlobalRepository.class);
        final MockEmailGlobalEntity mockEmailGlobalEntity = new MockEmailGlobalEntity();
        Mockito.when(emailGlobalRepository.save(Mockito.any())).thenReturn(mockEmailGlobalEntity.createGlobalEntity());
        final PropertyInitializer propertyInitializer = new PropertyInitializer();
        final ContentConverter contentConverter = new ContentConverter(new Gson(), new DefaultConversionService());
        final EmailGlobalContentConverter emailGlobalContentConverter = new EmailGlobalContentConverter(contentConverter);
        final EmailGlobalRepositoryAccessor emailGlobalRepositoryAccessor = new EmailGlobalRepositoryAccessor(emailGlobalRepository);
        final EntityPropertyMapper entityPropertyMapper = new EntityPropertyMapper();
        final List<Descriptor> descriptors = Arrays.asList(new EmailDescriptor(null, emailGlobalContentConverter, emailGlobalRepositoryAccessor, null, null, entityPropertyMapper));
        final AlertStartupInitializer initializer = new AlertStartupInitializer(propertyInitializer, descriptors, environment, conversionService);

        initializer.initializeConfigs();
        final AlertStartupProperty property = initializer.getAlertStartupProperties().get(0);
        final String value = "a system property value";
        System.setProperty(property.getPropertyKey(), value);
        initializer.initializeConfigs();
    }

    @Test
    public void testSetRestModelSecurityException() throws Exception {
        final Environment environment = Mockito.mock(Environment.class);
        final ConversionService conversionService = Mockito.mock(ConversionService.class);
        final EmailGlobalRepository emailGlobalRepository = Mockito.mock(EmailGlobalRepository.class);
        Mockito.when(conversionService.canConvert(Mockito.any(Class.class), Mockito.any(Class.class))).thenThrow(new SecurityException());
        final PropertyInitializer propertyInitializer = new PropertyInitializer();
        final ContentConverter contentConverter = new ContentConverter(new Gson(), new DefaultConversionService());
        final EmailGlobalContentConverter emailGlobalContentConverter = new EmailGlobalContentConverter(contentConverter);
        final EmailGlobalRepositoryAccessor emailGlobalRepositoryAccessor = new EmailGlobalRepositoryAccessor(emailGlobalRepository);
        final EntityPropertyMapper entityPropertyMapper = new EntityPropertyMapper();
        final List<Descriptor> descriptors = Arrays.asList(new EmailDescriptor(null, emailGlobalContentConverter, emailGlobalRepositoryAccessor, null, null, entityPropertyMapper));
        final AlertStartupInitializer initializer = new AlertStartupInitializer(propertyInitializer, descriptors, environment, conversionService);
        initializer.initializeConfigs();
    }

    @Test
    public void testSetRestModelIllegalArgumentException() throws Exception {
        final Environment environment = Mockito.mock(Environment.class);
        final ConversionService conversionService = Mockito.mock(ConversionService.class);
        final EmailGlobalRepository emailGlobalRepository = Mockito.mock(EmailGlobalRepository.class);
        Mockito.when(conversionService.canConvert(Mockito.any(Class.class), Mockito.any(Class.class))).thenThrow(new IllegalArgumentException());
        final PropertyInitializer propertyInitializer = new PropertyInitializer();
        final ContentConverter contentConverter = new ContentConverter(new Gson(), new DefaultConversionService());
        final EmailGlobalContentConverter emailGlobalContentConverter = new EmailGlobalContentConverter(contentConverter);
        final EmailGlobalRepositoryAccessor emailGlobalRepositoryAccessor = new EmailGlobalRepositoryAccessor(emailGlobalRepository);
        final EntityPropertyMapper entityPropertyMapper = new EntityPropertyMapper();
        final List<Descriptor> descriptors = Arrays.asList(new EmailDescriptor(null, emailGlobalContentConverter, emailGlobalRepositoryAccessor, null, null, entityPropertyMapper));
        final AlertStartupInitializer initializer = new AlertStartupInitializer(propertyInitializer, descriptors, environment, conversionService);
        initializer.initializeConfigs();
    }

    @Test
    public void testInitializeConfigsThrowsIllegalArgumentException() throws Exception {
        final Environment environment = Mockito.mock(Environment.class);
        final ConversionService conversionService = new DefaultConversionService();
        Mockito.doThrow(new IllegalArgumentException()).when(environment).getProperty(Mockito.anyString());
        final EmailGlobalRepository emailGlobalRepository = Mockito.mock(EmailGlobalRepository.class);
        throwExceptionTest(environment, conversionService, emailGlobalRepository);
    }

    @Test
    public void testInitializeConfigsThrowsSecurityException() throws Exception {
        final Environment environment = Mockito.mock(Environment.class);
        final ConversionService conversionService = new DefaultConversionService();
        Mockito.doThrow(new SecurityException()).when(environment).getProperty(Mockito.anyString());
        final EmailGlobalRepository emailGlobalRepository = Mockito.mock(EmailGlobalRepository.class);
        throwExceptionTest(environment, conversionService, emailGlobalRepository);
    }

    private void throwExceptionTest(final Environment environment, final ConversionService conversionService, final EmailGlobalRepository emailGlobalRepository) throws Exception {
        final PropertyInitializer propertyInitializer = new PropertyInitializer();
        final ContentConverter contentConverter = new ContentConverter(new Gson(), new DefaultConversionService());
        final EmailGlobalContentConverter emailGlobalContentConverter = new EmailGlobalContentConverter(contentConverter);
        final EmailGlobalRepositoryAccessor emailGlobalRepositoryAccessor = new EmailGlobalRepositoryAccessor(emailGlobalRepository);
        final EntityPropertyMapper entityPropertyMapper = new EntityPropertyMapper();
        final List<Descriptor> descriptors = Arrays.asList(new EmailDescriptor(null, emailGlobalContentConverter, emailGlobalRepositoryAccessor, null, null, entityPropertyMapper));
        final AlertStartupInitializer initializer = new AlertStartupInitializer(propertyInitializer, descriptors, environment, conversionService);
        initializer.initializeConfigs();
        assertFalse(initializer.getAlertPropertyNameSet().isEmpty());
    }
}
