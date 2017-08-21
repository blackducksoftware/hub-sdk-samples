# hub-sdk-samples

## Overview

Collection of classes that demonstrate how to execute certain Hub API processes In Java using the hub-common library. These processes include:

* Adding a user with a role
* Delete all unmapped code locations
* Get a component's matched files
* Get policy status for a component on the BOM
* Get vulnerability info for a component
* Get risk profile for an application/version

## Structure of samples

All samples are based off of `AbstractSample.java` , which contains two abstract methods and a `connect()`  method which simply connects to a Hub Server and initializes a `HubServicesFactory` object using that connection.

```java
public abstract void parseCommandLineArguments(String args[]);  
```

* Simply parses the command line and does a basic validation of the parsed arguments

```java
public abstract void execute() throws MalformedURLException, IntegrationException;
```

* Where the code that demonstrates how to perform a certain function resides. This is consistent across all samples



