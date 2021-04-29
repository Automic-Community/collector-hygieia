---
Title: CDA Collector
Sidebar: hygieia_sidebar
---

Configure the CDA Collector to display and monitor information (related to application deployments) on the Hygieia Dashboard, from CDA. Hygieia uses Spring Boot to package the collector as an executable JAR file with dependencies.

### Setup Instructions

## Fork and Clone the Collector 

Fork and clone the CDA Collector from the [GitHub repo](https://github.com/Hygieia/hygieia-deploy-CDA-collector). 

To configure the CDA Collector, execute the following steps:

*   **Step 1: Change Directory**

Change the current working directory to the `hygieia-deploy-CDA-collector` directory of your Hygieia source code installation.

For example, in the Windows command prompt, run the following command:

```
cd C:\Users\[username]\hygieia-deploy-CDA-collector
```

*   **Step 2: Run Maven Build**

Run the maven build to package the collector into an executable JAR file:

```
mvn install
```

The output file `[collector name].jar` is generated in the `hygieia-deploy-CDA-collector\target` folder.

*   **Step 3: Set Parameters in Application Properties File**

Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the CDA Collector.

To configure parameters for the CDA Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*   **Step 4: Deploy the Executable File**

To deploy the `[collector name].jar` file, change directory to `hygieia-deploy-CDA-collector\target`, and then execute the following from the command prompt:

```bash
java -jar [collector name].jar --spring.config.name=CDA --spring.config.location=[path to application.properties file]
```

### Sample Application Properties File

```properties
		# Database Name
		dbname=dashboarddb

		# Database HostName - default is localhost
		dbhost=10.97.101.128

		# Database Port - default is 27017
		dbport=27017

		# MongoDB replicaset
		dbreplicaset=[false if you are not using MongoDB replicaset]
		dbhostport=[host1:port1,host2:port2,host3:port3]

		# Database Username - default is blank
		dbusername=nmt_dashboarddb

		# Database Password - default is blank
		dbpassword=S3rv3r@Aut

		# Logging File location
		logging.file=./logs/cdadeploy.log

		# Collector schedule (required)
		cda.cron=0 0/5 * * * *

		# Cda server (required)
		cda.url=http://vviecd05/CDA
		cda.uiurl=http://vviecd05:8080/awi

		# Cda user name (required)
		cda.username=100/CD/CD

		# Cda password (required)
		cda.password=CD
```
