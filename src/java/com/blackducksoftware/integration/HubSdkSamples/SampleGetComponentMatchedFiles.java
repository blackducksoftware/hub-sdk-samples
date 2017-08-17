package com.blackducksoftware.integration.HubSdkSamples;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.aggregate.bom.AggregateBomRequestService;
import com.blackducksoftware.integration.hub.api.item.MetaService;
import com.blackducksoftware.integration.hub.api.project.ProjectRequestService;
import com.blackducksoftware.integration.hub.api.project.version.ProjectVersionRequestService;
import com.blackducksoftware.integration.hub.model.view.MatchedFilesView;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.model.view.ProjectView;
import com.blackducksoftware.integration.hub.model.view.VersionBomComponentView;
import com.blackducksoftware.integration.hub.request.HubPagedRequest;
import com.blackducksoftware.integration.hub.request.HubRequestFactory;
import com.blackducksoftware.integration.hub.rest.CredentialsRestConnection;
import com.blackducksoftware.integration.hub.service.HubResponseService;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.google.gson.Gson;

/**
 * Sample program to get the list of matched files for a component
 *
 */
public class SampleGetComponentMatchedFiles extends AbstractSample{
	
    private static String projectName;
    private static String versionName;
    private static String componentName;
    
    @Override
	public CredentialsRestConnection connect() throws MalformedURLException, IntegrationException {
		URL serverAddressURL = new URL(serverAddress);
		CredentialsRestConnection credentialsRestConnection = new CredentialsRestConnection(logger, serverAddressURL, username, password, timeOut);
		credentialsRestConnection.connect();
		credentialsRestConnection.logger = logger;
		return credentialsRestConnection;
	}
	
	@Override
	public void parseCommandLineArguments(String args[]){		
		try{
			serverAddress = args[0];
			username = args[1];
			password = args[2];
			projectName = args[3];
			versionName = args[4];	
			componentName = args[5];
		} catch (Exception e){
			System.out.println("Usage: java SampleGetComponentMatchedFiles.java serverAddress username password projectName versionName componentName");
			System.exit(1);
		}
	}
	
	/**
	 * Retrieves project given a project name
	 * @param projectName
	 * @return ProjectView project
	 * @throws IntegrationException
	 */
	public ProjectView getProject(String projectName) throws IntegrationException {
		ProjectRequestService projectRequestService = hubServicesFactory.createProjectRequestService(logger);
		try{
			ProjectView project = projectRequestService.getProjectByName(projectName);
			return project;
		} catch (IntegrationException e){
			System.out.println("Project " + projectName + " does not exist");
			System.exit(2);
		}
		return null;
	}
	
	/**
	 * Retrieves project version given a project name and version name
	 * @param projectName
	 * @param projectVersion name
	 * @return ProjectVersionView project
	 * @throws IntegrationException
	 */
	public ProjectVersionView getVersion(String projectName, String projectVersion) throws IntegrationException{
		ProjectVersionRequestService projectVersionRequestService = hubServicesFactory.createProjectVersionRequestService(logger);
		try{
			ProjectVersionView version = projectVersionRequestService.getProjectVersion(getProject(projectName), projectVersion);		
			return version;		
		} catch (IntegrationException e){
			System.out.println("Version " + versionName + " does not exist");
			System.exit(2);
		}
		return null;
	}
	
	@Override
	public void execute() throws MalformedURLException, IntegrationException{
		// parse command line arguments and connect
		CredentialsRestConnection credentialsRestConnection = connect();
		
		// create necessary services
		hubServicesFactory = new HubServicesFactory(credentialsRestConnection);
		HubRequestFactory hubRequestFactory = new HubRequestFactory(credentialsRestConnection);
		HubResponseService hubResponseService = new HubResponseService(credentialsRestConnection);	
		AggregateBomRequestService bomRequestService = hubServicesFactory.createAggregateBomRequestService(logger);
		MetaService metaService = hubServicesFactory.createMetaService(logger);
		
		// get project and version
		ProjectVersionView version = getVersion(projectName, versionName);
		
		// get components 
		final String bomUrl = metaService.getFirstLink(version, "components");
        final List<VersionBomComponentView> bomComponents = bomRequestService.getBomEntries(bomUrl);
        
        // search for component
        VersionBomComponentView bomComponent = null;
        for (VersionBomComponentView component : bomComponents){
        	if (component.componentName.equals(componentName)){
        		bomComponent = component;
        	}
        }
        if (bomComponent == null){
        	System.out.println("Component " + componentName + " does not exist");
        	System.exit(2);
        }
        
        //create gson and parse out matched files url.
        Gson gson = credentialsRestConnection.gson;
        Map<String, Object> javaRootMapObject = gson.fromJson(bomComponent.json, Map.class);
        String matchedFilesUrl = ((Map) ((List) ((Map) ((Map) ((List) javaRootMapObject.get("origins")).get(0)).get("_meta")).get("links")).get(1)).get("href").toString();
		
        // get matched files
		final HubPagedRequest hubPagedRequest = hubRequestFactory.createPagedRequest(matchedFilesUrl);
        final List<MatchedFilesView> allMatchedFiles = hubResponseService.getAllItems(hubPagedRequest, MatchedFilesView.class);
		
        // you now have a list of all matched files 
        System.out.println(allMatchedFiles);
		
	}
    
		
	public static void main(String[] args) throws Exception {
		SampleGetComponentMatchedFiles sampleGetComponentMatchedFiles = new SampleGetComponentMatchedFiles();
		sampleGetComponentMatchedFiles.parseCommandLineArguments(args);
		sampleGetComponentMatchedFiles.execute();
	}
}
