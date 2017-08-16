package com.blackducksoftware.integration.HubSdkSamples;

import java.net.MalformedURLException;
import java.net.URL;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.project.ProjectRequestService;
import com.blackducksoftware.integration.hub.api.project.version.ProjectVersionRequestService;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.model.view.ProjectView;
import com.blackducksoftware.integration.hub.rest.CredentialsRestConnection;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.test.TestLogger;

public class SampleAddComponentToBom {
	
	private static String serverAddress = "https://sup-hub01.dc1.lan";
    private static String username = "sysadmin";
    private static String password = "blackduck";
    private static HubServicesFactory hubServicesFactory;
    private static final IntLogger logger = new TestLogger();
    private static final int timeOut = 10000;

	public static void main(String[] args) throws MalformedURLException, IntegrationException {
		CredentialsRestConnection credentialsRestConnection = connect();
		hubServicesFactory = new HubServicesFactory(credentialsRestConnection);
		
		// hub-common does not support functionality
		
		
		
	}
	
	public static CredentialsRestConnection connect() throws MalformedURLException, IntegrationException {
		URL serverAddressURL = new URL(serverAddress);
		CredentialsRestConnection credentialsRestConnection = new CredentialsRestConnection(logger, serverAddressURL, username, password, timeOut);
		credentialsRestConnection.connect();
		credentialsRestConnection.logger = logger;
		return credentialsRestConnection;
	} //connect
	
	public static ProjectView getProject(String projectName) throws IntegrationException {
		ProjectRequestService projectRequestService = hubServicesFactory.createProjectRequestService(logger);
		ProjectView project = projectRequestService.getProjectByName(projectName);
		return project;
	} //getProject
	
	public static ProjectVersionView getVersion(String projectName, String projectVersion) throws IntegrationException{
		ProjectVersionRequestService projectVersionRequestService = hubServicesFactory.createProjectVersionRequestService(logger);
		ProjectVersionView version = projectVersionRequestService.getProjectVersion(getProject(projectName), projectVersion);		
		return version;		
	} //getVersion
}
