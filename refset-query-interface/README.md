#Refset rest server

##Technologies & Framework Used

Refset rest server is built with extensive set of open source technologies and frameworks. Main ones are listed as follows
 
JDK 1.7, Servlet

Maven

Spring Framework

Spring Security

Datastax Cassandra

Elastic Search

Titan DB

Tinker Pop blueprints API

Logback

Swgger

Jackson

Graphite


For more find grained details of each of the library used take a look at pom file.

## Backend

In order to support refset tool data storage, service is using Cassandra backed Titan db. It has two key spaces isolated to each other storing refset and terminology data respectively.


###Schema


###Indexes




##Build & Deployment

	mvn clean -DskipTests=true package install

##Run

	mvn -DRefsetConfig=src/main/resources/refset-dev.properties clean tomcat7:run

##API Documentation

	http://dev-refset.ihtsdotools.org:8080/refset/
	






&copy; IHTSDO

