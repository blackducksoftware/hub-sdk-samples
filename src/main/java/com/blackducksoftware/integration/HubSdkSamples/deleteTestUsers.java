package com.blackducksoftware.integration.HubSdkSamples;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.item.MetaService;
import com.blackducksoftware.integration.hub.api.role.RoleRequestService;
import com.blackducksoftware.integration.hub.api.user.UserRequestService;
import com.blackducksoftware.integration.hub.model.request.RoleAssignmentRequest;
import com.blackducksoftware.integration.hub.model.request.UserRequest;
import com.blackducksoftware.integration.hub.model.view.RoleAssignmentView;
import com.blackducksoftware.integration.hub.model.view.UserView;
import com.blackducksoftware.integration.hub.request.HubRequest;
import com.blackducksoftware.integration.hub.request.HubRequestFactory;
import com.blackducksoftware.integration.hub.rest.CredentialsRestConnection;
import com.blackducksoftware.integration.hub.service.HubResponseService;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.test.TestLogger;

public class deleteTestUsers {

	private static String serverAddress;
    private static String username;
    private static String password;
    private static HubServicesFactory hubServicesFactory;
    private static final IntLogger logger = new TestLogger();
    private static final int timeOut = 10000;
       
    
	/**
	 * Connects to server. Username, password, and server address taken from command line
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

		} 
		catch (Exception e){
			System.out.println("Usage: java SampleAddUserWithRole.java serverAddress username password");
			System.exit(1);
		}
	}
    
    
	public static void main(String[] args) throws MalformedURLException, IntegrationException {
		
		// connect and create hubServicesFactory
		parseCommandLineArguments(args);
		CredentialsRestConnection credentialsRestConnection = connect();
		hubServicesFactory = new HubServicesFactory(credentialsRestConnection);
		
		// create necessary services
		MetaService metaService = hubServicesFactory.createMetaService(logger);
		UserRequestService userRequestService = hubServicesFactory.createUserRequestService();
		HubRequestFactory hubRequestFactory = new HubRequestFactory(credentialsRestConnection);
		HubResponseService hubResponseService = new HubResponseService(credentialsRestConnection);
		RoleRequestService roleRequestService = hubServicesFactory.createRoleRequestService(logger);
		
        List<UserView> allUsers = userRequestService.getAllUsers();
        
        for (UserView currUser : allUsers){
        	if (currUser.firstName.startsWith("test")){
        		System.out.println(currUser.firstName);
        		List<RoleAssignmentView> allRoles = roleRequestService.getRolesByUserPublic(currUser);
        		for (RoleAssignmentView currRole : allRoles){
        			System.out.println(currRole);
        			roleRequestService.deleteRoleAssignmentForUserPublic(currUser, currRole);
        		}
        		UserView updatedUser = new UserView();
        		updatedUser.active = false;
        		updatedUser.email = currUser.email;
        		updatedUser.firstName = currUser.firstName;
        		updatedUser.lastName = currUser.lastName;
        		updatedUser.userName = currUser.userName;
        		
        		userRequestService.updateUser(updatedUser, metaService.getHref(currUser));
        	}
        }
		
		
	}

}
