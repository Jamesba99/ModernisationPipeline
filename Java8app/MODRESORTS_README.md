# ModResorts Demo Application

The aplication is based on the git repository
https://github.com/IBM/sample-app-mod but slightly modified.

## Overview
ModResorts is a IBM WebSphere Application Server web application. It is a simple application that can be used to demonstrate application modernization from IBM WebSphere Application Server traditional to Liberty, which is a modern cloud-ready enterprise Java runtime, as well as Java SE version upgrade scenarios.
The Java source code is dependent on APIs that only exist on the IBM WebSphere Application Server and as such, this version of the application will only function correctly when deployed to IBM WebSphere Application Server. In order to successfully deploy to Liberty, code changes need to be made to the application.


## Building

### IBM WebSphere Application Server Dependencies
The `main` branch version of ModResorts has dependencies on WebSphere Application Server APIs. The `pom.xml` references the associated WAS dependency and to build the application, you will need to have the dependency available in a maven repository. The `was_public.jar` jar and its associated `pom` file can be found in your WebSphere installation. For example, in a typical installation, you might find them at the following location: `/opt/WebSphere/AppServer/dev`.
You can install to your local maven repository (`$HOME/.m2`) using the following command:

```
mvn install:install-file -Dfile=<some location>/was_public.jar -DpomFile=<some location>/was_public-9.0.0.pom
```

For more information please see the [docs](https://www.ibm.com/docs/en/wasdtfe?topic=environment-installing-server-apis-into-maven-repository).

### Building Using Maven
This is a standard single module maven application and the WAR can be built as follows:

```
mvn clean package
```

## Deploying the Application to IBM WebSphere Application Server
There are no special instructions for deploying the application to IBM WebSphere Application Server. There is no configuration required on the application server in order for the application to deploy and function.

It can be deployed using the UI console or using `wsadmin`.
See the [documentation](https://www.ibm.com/docs/en/was-nd/9.0.5?topic=applications-how-do-i-deploy) for more details on deploying the application to WebSphere Application Server.

If you want to configure the weather API URL in traditional WAS, you can use a wsadmin script setURLProvider.py. To install the application to server1, use the script modresorts_install.py.


## Deploying the Application to Liberty
The application as is wil not work on Liberty. Therefore you need to modernize it to get rid of proprietary APIs.

If you want to configure the weather API URL in Liberty, add the following line to the server.xml :
<jndiURLEntry id="WeatherAPI_URLProvider_1" jndiName="url/WeatherAPI" value="http://api.wunderground.com/api"/> 


## Migration Plan
The `ama` directory contains a migration plan for ModResorts created by [IBM Application Modernization Accelerator](https://www.ibm.com/products/jsphere/tools) (AMA). It is used by IBM AMA Dev Tools (available for VS Code, Eclipse IDE, and IntelliJ IDEA) or IBM Bob to accelerate modernization of legacy enterprise Java applications to run on Liberty.  [Try](https://www.ibm.com/account/reg/us-en/signup?formid=urx-53705) AMA.
