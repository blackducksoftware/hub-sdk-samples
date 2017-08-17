/**
 * Hub Common
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.api.role;

import java.util.List;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.item.MetaService;
import com.blackducksoftware.integration.hub.model.request.RoleAssignmentRequest;
import com.blackducksoftware.integration.hub.model.view.RoleAssignmentView;
import com.blackducksoftware.integration.hub.model.view.UserView;
import com.blackducksoftware.integration.hub.request.HubPagedRequest;
import com.blackducksoftware.integration.hub.request.HubRequest;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.service.HubResponseService;

import okhttp3.Response;

public class RoleRequestService extends HubResponseService {
	
	private final MetaService metaService;

	public RoleRequestService(final RestConnection restConnection, final MetaService metaService) {
	    super(restConnection);
	    this.metaService = metaService;
	}
	

	public List<RoleAssignmentView> getRolesByUserPublic(final UserView user) throws IntegrationException{
		final String rolesUrl = metaService.getFirstLink(user, MetaService.ROLES_LINK);
		final HubPagedRequest hubPagedRequest = getHubRequestFactory().createPagedRequest(100, rolesUrl);
		
		final List<RoleAssignmentView> allRoleMatchingItems = getAllItems(hubPagedRequest, RoleAssignmentView.class);
		return allRoleMatchingItems;
	}
	
	public List<UserView> getUsersByRolePublic(final RoleAssignmentView role) throws IntegrationException{
		final String roleUsersUrl = metaService.getFirstLink(role, MetaService.ROLE_USERS_LINK);
		HubPagedRequest hubPagedRequest = getHubRequestFactory().createPagedRequest(100, roleUsersUrl);
		
		final List<UserView> allUsersWithRole = getAllItems(hubPagedRequest, UserView.class);
		return allUsersWithRole;
	}
	
	public String addRoleForUserPublic(final UserView user, final RoleAssignmentRequest role) throws IntegrationException{
		final String rolesUrl = metaService.getFirstLink(user, MetaService.ROLES_LINK);
		final HubRequest hubRequest = getHubRequestFactory().createRequest(rolesUrl);
		Response response = null;
		try {
			response = hubRequest.executePost(getGson().toJson(role));
			return response.header("location");
		} finally {
			if (response != null){
				response.close();
			}
		}
	}
	
	public void deleteRoleAssignmentForUserPublic(final UserView user, final RoleAssignmentView role) throws IntegrationException {
		final String rolesUrl = metaService.getFirstLink(user, MetaService.ROLES_LINK);
		final HubPagedRequest hubPagedRequest = getHubRequestFactory().createPagedRequest(100, rolesUrl);
		final List<RoleAssignmentView> allRoleMatchingItems = getAllItems(hubPagedRequest, RoleAssignmentView.class);
		
		for (RoleAssignmentView roleView : allRoleMatchingItems){
			if (roleView.equals(role)){
				final HubRequest deleteRequest = getHubRequestFactory().createRequest(metaService.getHref(role));
				deleteRequest.executeDelete();
			}
		}
	}
	
}
