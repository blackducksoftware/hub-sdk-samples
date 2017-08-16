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
import com.blackducksoftware.integration.hub.request.HubRequestFactory;
import com.blackducksoftware.integration.hub.rest.CredentialsRestConnection;
import com.blackducksoftware.integration.hub.service.HubResponseService;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.test.TestLogger;

/**
 * Sample program to create a user and add a role. (uses Role Name, not roleID/URL)
 *
 */
public class SampleAddUserWithRole {
	
	private static String serverAddress;
    private static String username;
    private static String password;
    private static Boolean newActive;
    private static String email;
    private static String newFirstName;
    private static String newLastName;
    private static String newUserName;
    private static String newPassword;
    private static String roleName;
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
			newActive = args[3].startsWith("t") || args[3].startsWith("T") ? true : false;	// if it starts with a t, newActive = true, otherwise false;
			email = args[4];
			newFirstName = args[5];
			newLastName = args[6];
			newUserName = args[7];
			newPassword = args[8];
			roleName = args[9];
		} 
		catch (Exception e){
			System.out.println("Usage: java SampleAddUserWithRole.java serverAddress username password newActive email newFirstName newLastName newUserName newPassword roleName");
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
		
		// create userRequest and get all list of all users
		UserRequest userRequest = new UserRequest(newActive, email, newFirstName, newLastName, newUserName, newPassword);
		userRequestService.createUser(userRequest);
        List<UserView> allUsers = userRequestService.getAllUsers();
		
        // search for the user that was just created
        UserView user = null;
        for (UserView userView : allUsers){
        	if (userView.firstName.equals(userRequest.firstName)){
        		user = userView;
        	}
        }
		
        // get list of all possible roles
		final List<RoleAssignmentView> allRoles = hubResponseService.getAllItems(serverAddress + "/api/roles", RoleAssignmentView.class);

		// find desired role
		RoleAssignmentRequest roleAssignmentRequest = new RoleAssignmentRequest();
		for (RoleAssignmentView roleAssignmentView : allRoles){		
			if (roleAssignmentView.name.equals(roleName)){
				roleAssignmentRequest.role = metaService.getHref(roleAssignmentView);
				break;
			}
		}
		
		// add role to user
		roleRequestService.addRoleForUserPublic(user, roleAssignmentRequest);
		
		
	}
	
}
