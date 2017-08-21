package com.blackducksoftware.integration.HubSdkSamples;

import java.net.MalformedURLException;
import java.util.List;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.codelocation.CodeLocationRequestService;
import com.blackducksoftware.integration.hub.model.view.CodeLocationView;


/**
 * Sample program to delete all unmapped code locations
 *
 */
public class SampleDeleteUnmappedCodeLocations extends AbstractSample{
		
	@Override
	public void parseCommandLineArguments(String args[]){		
		try{
			serverAddress = args[0];
			username = args[1];
			password = args[2];

		} catch (Exception e){
			System.out.println("Usage: java SampleDeleteUnmappedCodeLocations.java serverAddress username password");
			System.exit(1);
		}
	}
	
	
	@Override
	public void execute() throws MalformedURLException, IntegrationException{
		
		// create necessary services	
		CodeLocationRequestService codeLocationRequestService = hubServicesFactory.createCodeLocationRequestService(logger);
		
		// retrieve all code location. Loop through and delete any code locations that do not have a mappedProjectVersion
		List<CodeLocationView> allCodeLocations = codeLocationRequestService.getAllCodeLocations();
		for (CodeLocationView codeLocationView : allCodeLocations){
			if (codeLocationView.mappedProjectVersion == null){
				System.out.println(codeLocationView.name);
				codeLocationRequestService.deleteCodeLocation(codeLocationView);
			}
		}
		
		// all unmapped code locations should have been deleted	
	}
	
	public static void main(String[] args) throws MalformedURLException, IntegrationException {
		SampleDeleteUnmappedCodeLocations sampleDeleteUnmappedCodeLocations = new SampleDeleteUnmappedCodeLocations();
		sampleDeleteUnmappedCodeLocations.parseCommandLineArguments(args);
		sampleDeleteUnmappedCodeLocations.connect();
		sampleDeleteUnmappedCodeLocations.execute();
	}
}
