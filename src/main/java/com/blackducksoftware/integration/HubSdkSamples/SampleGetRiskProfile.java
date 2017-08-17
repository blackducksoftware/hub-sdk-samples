/**
 * SampleGetRiskProfile
 * Gets risk profile for an application/version
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

package com.blackducksoftware.integration.HubSdkSamples;

import java.net.MalformedURLException;
import java.net.URL;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.project.ProjectRequestService;
import com.blackducksoftware.integration.hub.api.project.version.ProjectVersionRequestService;
import com.blackducksoftware.integration.hub.dataservice.report.RiskReportDataService;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.model.view.ProjectView;
import com.blackducksoftware.integration.hub.report.api.ReportData;
import com.blackducksoftware.integration.hub.rest.CredentialsRestConnection;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;

/**
 * Sample program to how to get the risk report data for a project and version
 *
 *
 */
public class SampleGetRiskProfile extends AbstractSample{
	
	private static String projectName;
	private static String versionName;
    
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
		} catch (Exception e){
			System.out.println("Usage: java SampleGetRiskProfile.java serverAddress username password projectName versionName");
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
	
	/**
	 * Creates a riskReportDataService and returns the risk report data given a project and version.
	 * @param project
	 * @param version
	 * @return risk report
	 * @throws IntegrationException
	 */
	public ReportData getRiskProfile(ProjectView project, ProjectVersionView version) throws IntegrationException {
		RiskReportDataService riskReportDataService = hubServicesFactory.createRiskReportDataService(logger, 10000);		
		return riskReportDataService.getRiskReportData(project, version);
	}
    
	@Override
	public void execute() throws IntegrationException, MalformedURLException{
		// parse command line arguments, connect and initialize hubServicesFactory
		CredentialsRestConnection credentialsRestConnection = connect();
		hubServicesFactory = new HubServicesFactory(credentialsRestConnection);
		
		//get risk report Data. ReportData class fields contain risk information.
		ReportData reportData = getRiskProfile(getProject(projectName), getVersion(projectName, versionName));
		System.out.println(reportData.getLicenseRiskHighCount());
		System.out.println(reportData.getProjectName());
	}
	
	
	public static void main(String[] args) throws MalformedURLException, IntegrationException {		
		SampleGetRiskProfile sampleGetRiskProfile = new SampleGetRiskProfile();
		sampleGetRiskProfile.parseCommandLineArguments(args);
		sampleGetRiskProfile.execute();		
	}
}
