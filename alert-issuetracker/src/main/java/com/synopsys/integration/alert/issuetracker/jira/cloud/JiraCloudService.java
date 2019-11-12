package com.synopsys.integration.alert.issuetracker.jira.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.alert.common.exception.AlertException;
import com.synopsys.integration.alert.issuetracker.IssueTrackerContext;
import com.synopsys.integration.alert.issuetracker.IssueTrackerService;
import com.synopsys.integration.alert.issuetracker.jira.cloud.util.JiraCloudIssueHandler;
import com.synopsys.integration.alert.issuetracker.jira.cloud.util.JiraCloudIssuePropertyHandler;
import com.synopsys.integration.alert.issuetracker.jira.cloud.util.JiraCloudTransitionHandler;
import com.synopsys.integration.alert.issuetracker.jira.common.JiraConstants;
import com.synopsys.integration.alert.issuetracker.jira.common.JiraMessageParser;
import com.synopsys.integration.alert.issuetracker.message.IssueTrackerMessageResult;
import com.synopsys.integration.alert.issuetracker.message.IssueTrackerRequest;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.jira.common.cloud.service.IssueSearchService;
import com.synopsys.integration.jira.common.cloud.service.IssueService;
import com.synopsys.integration.jira.common.cloud.service.JiraCloudServiceFactory;
import com.synopsys.integration.jira.common.cloud.service.ProjectService;
import com.synopsys.integration.jira.common.cloud.service.UserSearchService;
import com.synopsys.integration.jira.common.rest.service.IssueMetaDataService;
import com.synopsys.integration.jira.common.rest.service.IssuePropertyService;
import com.synopsys.integration.jira.common.rest.service.IssueTypeService;
import com.synopsys.integration.jira.common.rest.service.PluginManagerService;

public class JiraCloudService extends IssueTrackerService {
    private Logger logger = LoggerFactory.getLogger(JiraCloudService.class);

    public JiraCloudService(Gson gson) {
        super(gson, new JiraMessageParser());
    }

    @Override
    public IssueTrackerMessageResult sendMessage(IssueTrackerRequest request) throws IntegrationException {
        IssueTrackerContext context = request.getContext();
        JiraProperties jiraProperties = (JiraProperties) context.getIssueTrackerConfig();
        JiraCloudServiceFactory jiraCloudServiceFactory = jiraProperties.createJiraServicesCloudFactory(logger, getGson());
        PluginManagerService jiraAppService = jiraCloudServiceFactory.createPluginManagerService();
        logger.debug("Verifying the required application is installed on the Jira Cloud server...");
        boolean missingApp = jiraAppService.getInstalledApp(jiraProperties.getUsername(), jiraProperties.getAccessToken(), JiraConstants.JIRA_APP_KEY).isEmpty();
        if (missingApp) {
            throw new AlertException("Please configure the Jira Cloud plugin for your server instance via the global Jira Cloud channel settings.");
        }

        ProjectService projectService = jiraCloudServiceFactory.createProjectService();
        UserSearchService userSearchService = jiraCloudServiceFactory.createUserSearchService();
        IssueTypeService issueTypeService = jiraCloudServiceFactory.createIssueTypeService();
        IssueMetaDataService issueMetaDataService = jiraCloudServiceFactory.createIssueMetadataService();

        JiraCloudIssueConfigValidator jiraIssueConfigValidator = new JiraCloudIssueConfigValidator(projectService, userSearchService, issueTypeService, issueMetaDataService);
        jiraIssueConfigValidator.validate(context);

        IssueService issueService = jiraCloudServiceFactory.createIssueService();
        IssuePropertyService issuePropertyService = jiraCloudServiceFactory.createIssuePropertyService();
        IssueSearchService issueSearchService = jiraCloudServiceFactory.createIssueSearchService();
        JiraCloudTransitionHandler jiraTransitionHandler = new JiraCloudTransitionHandler(issueService);
        JiraCloudIssuePropertyHandler jiraIssuePropertyHandler = new JiraCloudIssuePropertyHandler(issueSearchService, issuePropertyService);
        JiraCloudIssueHandler jiraIssueHandler = new JiraCloudIssueHandler(issueService, jiraProperties, getMessageParser(), getGson(), jiraTransitionHandler, jiraIssuePropertyHandler);
        return jiraIssueHandler.createOrUpdateIssues(context.getIssueConfig(), request.getRequestContent());
    }
}
