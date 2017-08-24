package com.blackducksoftware.integration.HubSdkSamples;

import java.net.MalformedURLException;
import java.util.List;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.item.MetaService;
import com.blackducksoftware.integration.hub.api.role.RoleRequestService;
import com.blackducksoftware.integration.hub.api.user.CreateUserRequestService;
import com.blackducksoftware.integration.hub.model.request.RoleAssignmentRequest;
import com.blackducksoftware.integration.hub.model.request.UserRequest;
import com.blackducksoftware.integration.hub.model.view.RoleAssignmentView;
import com.blackducksoftware.integration.hub.model.view.UserView;
import com.blackducksoftware.integration.hub.service.HubResponseService;

/**
 * Sample program to create a user and add a role. (uses Role Name, not roleID/URL)
 *
 */
public class SampleAddUserWithRole extends AbstractSample {

    private static Boolean newActive;
    private static String email;
    private static String newFirstName;
    private static String newLastName;
    private static String newUserName;
    private static String newPassword;
    private static String roleName;

    @Override
    public void parseCommandLineArguments(final String args[]) {
        try {
            serverAddress = args[0];
            username = args[1];
            password = args[2];
            newActive = args[3].startsWith("t") || args[3].startsWith("T") ? true : false; // if it starts with a t, newActive = true, otherwise false;
            email = args[4];
            newFirstName = args[5];
            newLastName = args[6];
            newUserName = args[7];
            newPassword = args[8];
            roleName = args[9];
        } catch (final Exception e) {
            System.out.println("Usage: java SampleAddUserWithRole.java serverAddress username password newActive email newFirstName newLastName newUserName newPassword roleName");
            System.exit(1);
        }
    }

    @Override
    public void execute() throws IntegrationException, MalformedURLException {

        // create necessary services
        final MetaService metaService = hubServicesFactory.createMetaService(logger);
        final CreateUserRequestService userRequestService = new CreateUserRequestService(hubServicesFactory.getRestConnection());
        final HubResponseService hubResponseService = new HubResponseService(credentialsRestConnection);
        final RoleRequestService roleRequestService = new RoleRequestService(hubServicesFactory.getRestConnection(), metaService);

        // create userRequest and get all list of all users
        final UserRequest userRequest = new UserRequest(newActive, email, newFirstName, newLastName, newUserName, newPassword);
        userRequestService.createUser(userRequest);
        final List<UserView> allUsers = userRequestService.getAllUsers();

        // search for the user that was just created
        UserView user = null;
        for (final UserView userView : allUsers) {
            if (userView.firstName.equals(userRequest.firstName)) {
                user = userView;
            }
        }

        // get list of all possible roles
        final List<RoleAssignmentView> allRoles = hubResponseService.getAllItems(serverAddress + "/api/roles", RoleAssignmentView.class);

        // find desired role
        RoleAssignmentRequest roleAssignmentRequest = null;
        for (final RoleAssignmentView roleAssignmentView : allRoles) {
            if (roleAssignmentView.name.equals(roleName)) {
                roleAssignmentRequest = new RoleAssignmentRequest();
                roleAssignmentRequest.role = metaService.getHref(roleAssignmentView);
                break;
            }
        }
        if (roleAssignmentRequest == null) {
            System.out.println("role " + roleName + " does not exist");
            System.exit(2);
        }

        // add role to user
        roleRequestService.addRoleForUserPublic(user, roleAssignmentRequest);
    }

    public static void main(final String[] args) throws MalformedURLException, IntegrationException {
        final SampleAddUserWithRole sampleAddUserWithRole = new SampleAddUserWithRole();
        sampleAddUserWithRole.parseCommandLineArguments(args);
        sampleAddUserWithRole.connect();
        sampleAddUserWithRole.execute();
    }

}
