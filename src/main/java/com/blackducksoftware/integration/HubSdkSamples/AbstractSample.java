package com.blackducksoftware.integration.HubSdkSamples;

import java.net.MalformedURLException;
import java.net.URL;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.rest.CredentialsRestConnection;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.test.TestLogger;


/**
 * Abstract base class for all samples
 * @author mpeng
 *
 */
public abstract class AbstractSample {
	
	String serverAddress;
	String username;
	String password;
	HubServicesFactory hubServicesFactory;
	CredentialsRestConnection credentialsRestConnection;
    IntLogger logger = new TestLogger();
    final int timeOut = 10000;
    
    
	/**
	 * Parses command line arguments.
	 * @param args
	 */ 
    public abstract void parseCommandLineArguments(String args[]);
    
	/**
	 * Connects to server. Username, password, and server address taken from command line.
	 * @return credentialsRestConnection
	 * @throws MalformedURLException
	 * @throws IntegrationException
	 */
    public void connect() throws MalformedURLException, IntegrationException {
		URL serverAddressURL = new URL(serverAddress);
		credentialsRestConnection = new CredentialsRestConnection(logger, serverAddressURL, username, password, timeOut);
		credentialsRestConnection.connect();
		credentialsRestConnection.logger = logger;
		hubServicesFactory = new HubServicesFactory(credentialsRestConnection);
    }
    
    /**
     * Executes sample program.
     * @throws MalformedURLException
     * @throws IntegrationException
     */
    public abstract void execute() throws MalformedURLException, IntegrationException;
   
}
