package com.blackducksoftware.integration.HubSdkSamples;

import java.net.MalformedURLException;
import java.util.List;

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
import com.blackducksoftware.integration.hub.service.HubResponseService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Sample program to get the list of matched files for a component
 *
 */
public class SampleGetComponentMatchedFiles extends AbstractSample{
	
    private static String projectName;
    private static String versionName;
    private static String componentName;
	
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
		
		// create necessary services
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
        JsonObject object = gson.fromJson(bomComponent.json, JsonObject.class);
        String matchedFilesUrl = object.getAsJsonArray("origins")
        								.get(0).getAsJsonObject()
        								.getAsJsonObject("_meta")
        								.getAsJsonArray("links")
        								.get(1).getAsJsonObject()
        								.get("href")
        								.getAsString();
		
        // get matched files
		final HubPagedRequest hubPagedRequest = hubRequestFactory.createPagedRequest(matchedFilesUrl);
        final List<MatchedFilesView> allMatchedFiles = hubResponseService.getAllItems(hubPagedRequest, MatchedFilesView.class);
		
        // you now have a list of all matched files 
        System.out.println(allMatchedFiles);
		
	}
    
		
	public static void main(String[] args) throws Exception {
		SampleGetComponentMatchedFiles sampleGetComponentMatchedFiles = new SampleGetComponentMatchedFiles();
		sampleGetComponentMatchedFiles.parseCommandLineArguments(args);
		sampleGetComponentMatchedFiles.connect();
		sampleGetComponentMatchedFiles.execute();
	}
}
