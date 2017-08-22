package com.blackducksoftware.integration.hub.api.user;

import static com.blackducksoftware.integration.hub.api.UrlConstants.SEGMENT_API;
import static com.blackducksoftware.integration.hub.api.UrlConstants.SEGMENT_USERS;

import java.util.Arrays;
import java.util.List;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.user.UserRequestService;
import com.blackducksoftware.integration.hub.model.request.UserRequest;
import com.blackducksoftware.integration.hub.request.HubRequest;
import com.blackducksoftware.integration.hub.rest.RestConnection;

import okhttp3.Response;

public class CreateUserRequestService extends UserRequestService{
	
	private static final List<String> USERS_SEGMENTS = Arrays.asList(SEGMENT_API, SEGMENT_USERS);

	public CreateUserRequestService(RestConnection restConnection) {
		super(restConnection);
	}
	
	 public String createUser(final UserRequest user) throws IntegrationException {
	    final HubRequest UserItemRequest = getHubRequestFactory().createRequest(USERS_SEGMENTS);
	    Response response = null;
	    try {
	        response = UserItemRequest.executePost(getGson().toJson(user));
	        return response.header("location");
	    } finally {
	        if (response != null) {
	            response.close();
	        }
	    }
	 }
}
