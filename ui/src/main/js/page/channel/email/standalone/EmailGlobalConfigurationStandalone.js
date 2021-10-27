import React, { useState } from 'react';
import * as PropTypes from 'prop-types';
import CommonGlobalConfiguration from 'common/global/CommonGlobalConfiguration';
import {
    EMAIL_GLOBAL_FIELD_KEYS, EMAIL_INFO, EMAIL_TEST_FIELD
} from 'page/channel/email/EmailModels';
import TextInput from 'common/input/TextInput';
import * as HttpErrorUtilities from 'common/util/httpErrorUtilities';
import CheckboxInput from 'common/input/CheckboxInput';
import PasswordInput from 'common/input/PasswordInput';
import ConfigurationForm from 'page/channel/email/standalone/ConfigurationForm';
import * as ConfigurationRequestBuilder from 'common/util/configurationRequestBuilder';
import * as fieldModelUtilities from 'common/util/fieldModelUtilities';

const EmailGlobalConfigurationStandalone = ({
    csrfToken, errorHandler, readonly, displayTest, displaySave, displayDelete
}) => {
    const [emailConfig, setEmailConfig] = useState({});
    const [errors, setErrors] = useState(HttpErrorUtilities.createEmptyErrorObject());
    const [testEmailAddress, setTestEmailAddress] = useState(undefined);
    const emailRequestUrl = `${ConfigurationRequestBuilder.CONFIG_API_URL}/email`;

    const testField = (
        <TextInput
            id={EMAIL_TEST_FIELD.key}
            name={EMAIL_TEST_FIELD.key}
            label={EMAIL_TEST_FIELD.label}
            description={EMAIL_TEST_FIELD.description}
            onChange={(value) => setTestEmailAddress(value)}
            value={testEmailAddress}
        />
    );

    const fetchData = async () => {
        const response = await ConfigurationRequestBuilder.createReadRequest(emailRequestUrl, csrfToken);
        const data = await response.json();

        const { models } = data;
        if (models && models.length > 0) {
            setEmailConfig(models[0]);
        } else {
            setEmailConfig({});
        }
    };

    return (
        <CommonGlobalConfiguration
            label={`${EMAIL_INFO.label} Beta (WIP)`}
            description="Configure the email server that Alert will send emails to. (WIP: Everything on this page is currently in development)"
            lastUpdated={emailConfig.lastUpdated}
        >
            <ConfigurationForm
                csrfToken={csrfToken}
                formDataId={emailConfig.id}
                setErrors={(formErrors) => setErrors(formErrors)}
                testFields={testField}
                clearTestForm={() => setTestEmailAddress(undefined)}
                buttonIdPrefix={EMAIL_INFO.key}
                getRequest={fetchData}
                deleteRequest={() => ConfigurationRequestBuilder.createDeleteRequest(emailRequestUrl, csrfToken, emailConfig.id)}
                updateRequest={() => ConfigurationRequestBuilder.createUpdateRequest(emailRequestUrl, csrfToken, emailConfig.id, emailConfig)}
                createRequest={() => ConfigurationRequestBuilder.createNewConfigurationRequest(emailRequestUrl, csrfToken, emailConfig)}
                validateRequest={() => ConfigurationRequestBuilder.createValidateRequest(emailRequestUrl, csrfToken, emailConfig)}
                testRequest={() => ConfigurationRequestBuilder.createTestRequest(emailRequestUrl, csrfToken, emailConfig)}
                readonly={readonly}
                displayTest={displayTest}
                displaySave={displaySave}
                displayDelete={displayDelete}
                errorHandler={errorHandler}
            >
                <TextInput
                    id={EMAIL_GLOBAL_FIELD_KEYS.host}
                    name="smtpHost"
                    label="SMTP Host"
                    description="The host name of the SMTP email server."
                    required
                    readOnly={readonly}
                    onChange={fieldModelUtilities.handleTestChange(emailConfig, setEmailConfig)}
                    value={emailConfig.smtpHost || undefined}
                    errorName="smtpHost"
                    errorValue={errors.fieldErrors.host}
                />
                <TextInput
                    id={EMAIL_GLOBAL_FIELD_KEYS.from}
                    name="smtpFrom"
                    label="SMTP From"
                    description="The email address to use as the return address."
                    required
                    readOnly={readonly}
                    onChange={fieldModelUtilities.handleTestChange(emailConfig, setEmailConfig)}
                    value={emailConfig.smtpFrom || undefined}
                    errorName="smtpFrom"
                    errorValue={errors.fieldErrors.from}
                />
                <CheckboxInput
                    id={EMAIL_GLOBAL_FIELD_KEYS.auth}
                    name="smtpAuth"
                    label="SMTP Auth"
                    description="Select this if your SMTP server requires authentication, then fill in the SMTP User and SMTP Password."
                    readOnly={readonly}
                    onChange={fieldModelUtilities.handleTestChange(emailConfig, setEmailConfig)}
                    isChecked={(emailConfig.smtpAuth || 'false').toString().toLowerCase() === 'true'}
                    errorName="smtpAuth"
                    errorValue={errors.fieldErrors.smtpAuth}
                />
                <TextInput
                    id={EMAIL_GLOBAL_FIELD_KEYS.user}
                    name="smtpUsername"
                    label="SMTP User"
                    description="The username to authenticate with the SMTP server."
                    readOnly={readonly}
                    onChange={fieldModelUtilities.handleTestChange(emailConfig, setEmailConfig)}
                    value={emailConfig.smtpUsername || undefined}
                    errorName="smtpUsername"
                    errorValue={errors.fieldErrors.user}
                />
                <PasswordInput
                    id={EMAIL_GLOBAL_FIELD_KEYS.password}
                    name="smtpPassword"
                    label="SMTP Password"
                    description="The password to authenticate with the SMTP server."
                    readOnly={readonly}
                    onChange={fieldModelUtilities.handleTestChange(emailConfig, setEmailConfig)}
                    value={emailConfig.smtpPassword || undefined}
                    isSet={emailConfig.smtpPassword}
                    errorName="smtpPassword"
                    errorValue={errors.fieldErrors.password}
                />
                TODO: Add new custom properties field
            </ConfigurationForm>
        </CommonGlobalConfiguration>
    );
};

EmailGlobalConfigurationStandalone.propTypes = {
    csrfToken: PropTypes.string.isRequired,
    errorHandler: PropTypes.object.isRequired,
    // Pass this in for now while we have all descriptors in global state, otherwise retrieve this in this component
    readonly: PropTypes.bool,
    displayTest: PropTypes.bool,
    displaySave: PropTypes.bool,
    displayDelete: PropTypes.bool
};

EmailGlobalConfigurationStandalone.defaultProps = {
    readonly: false,
    displayTest: true,
    displaySave: true,
    displayDelete: true
};

export default EmailGlobalConfigurationStandalone;
