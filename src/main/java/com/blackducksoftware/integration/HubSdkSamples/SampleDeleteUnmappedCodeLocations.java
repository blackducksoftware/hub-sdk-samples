package com.blackducksoftware.integration.HubSdkSamples;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.codelocation.CodeLocationRequestService;
import com.blackducksoftware.integration.hub.model.view.CodeLocationView;
import com.blackducksoftware.integration.hub.rest.CredentialsRestConnection;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.test.TestLogger;


/**
 * Sample program to delete all unmapped code locations
 * @author mpeng
 *
 */
public class SampleDeleteUnmappedCodeLocations {

	private static String serverAddress;
    private static String username;
    private static String password;
    private static HubServicesFactory hubServicesFactory;
    private static final IntLogger logger = new TestLogger();
    private static final int timeOut = 10000;
    
	/**
	 * Connects to server.
	 * @return credentialsRestConnection
	 * @throws MalformedURLException
	 * @throws IntegrationException
	 */
	public static CredentialsRestConnection connect() throws MalformedURLException, IntegrationException {
		URL serverAddressURL = new URL(serverAddress);
		CredentialsRestConnection credentialsRestConnection = new CredentialsRestConnection(logger, serverAddressURL, username, password, timeOut);
		credentialsRestConnection.connect();
		credentialsRestConnection.logger = logger;
		return credentialsRestConnection;
	}
	
	/**
	 * Parses command line arguments.
	 * @param args
	 */
	public static void parseCommandLineArguments(String args[]){		
		try{
			serverAddress = args[0];
			username = args[1];
			password = args[2];

		} catch (Exception e){
			System.out.println("Usage: java SampleDeleteUnmappedCodeLocations.java serverAddress username password");
			System.exit(1);
		}
	}
	
	public static void main(String[] args) throws MalformedURLException, IntegrationException {
		//connect, and create hubServicesFactory and codeLocationRequestService
		parseCommandLineArguments(args);
		CredentialsRestConnection credentialsRestConnection = connect();
		hubServicesFactory = new HubServicesFactory(credentialsRestConnection);
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
}
