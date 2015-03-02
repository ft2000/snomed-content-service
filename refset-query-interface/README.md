![alt text](http://www.ihtsdo.org/images/small_logo.png "IHTSDO's Refset Rest Server")

#Refset Rest Server

---
##Technologies & Framework Used

Refset rest server is built with extensive set of open source libraries and frameworks. Main ones are listed as follows

|  Technology | version   |  Remark |

|---|:---|:---|

|  JDK |  v1.7 |   |

| Servlet | v3.x |  |

|  Maven |  v3.2.2 |   |

|Spring Framework | v4.x | |

|Spring Security |v3.x| |

|Datastax Cassandra |v2.0.10| |

|Elastic Search| v1.2.1| |

|Titan DB v0.5.x| |

|Tinker Pop blueprints API |v2.5.x| |

|Logback |v1.1.2| |

|Swagger |v2.x | |

|Jackson JSON Processor |v2.x| |

    
    
Operational matrix can be enabled and viewed using Graphite or similar tool 

For more find grained details of each of the library used take a look at pom file.

---
## Backend

In order to support refset tool data storage, this service is using Cassandra backed Titan db. 
It has two keyspaces namely 'snomed' and 'refset'. 'snomed' keyspace is consist of SNOMED® terminology graph. 'refset' keysapce is consist of refset graph.

Both keyspaces have corresponding elastic search index with identical index names. Both 'snomed' and 'refset' graphs are isolated to each other. 

---
###Schema

Graphs are required to have appropriate schemas. Schema must be created before any storage to any of graphs. Java programs which basically uses low level blueprint api, are used to create these schemas. These program are safe to run any time as before creating any schema element program verifies its availability. 

Initial 'snomed' schema can be created using following command


	java -jar snomed-graph-indexer-0.1.1-SNAPSHOT.jar snomed-graph-es-dev.properties schema

Initial 'refset' schema can be created using following command
 
	java -jar refset-graph-indexer-0.1.1-SNAPSHOT.jar refset-graph-es-dev.properties schema

---
###Indexes

Graph also need to have relevant indexes. 

Initial 'snomed' index can be created using following command
 
	java -jar snomed-graph-indexer-0.1.1-SNAPSHOT.jar snomed-graph-es-dev.properties index

Initial 'refset' index can be created using following command

	java -jar refset-graph-indexer-0.1.1-SNAPSHOT.jar refset-graph-es-dev.properties index

There are updates to 'refset' index which needs to run as follows

	java -jar refset-graph-indexer-0.1.1-SNAPSHOT.jar refset-graph-es-dev.properties update

---
##Data Load

Application would not work unless it has SNOMED® terminology data. Same can be loaded using below commands.
 
 
	java -jar snomed-graph-loader-0.1.1-SNAPSHOT.jar -config snomed-graph-es-dev.properties -type full -bSize 10000 -cf sct2_Concept_Snapshot_INT_20150131.txt >> concept.log 

	java -jar snomed-graph-loader-0.1.1-SNAPSHOT.jar -config snomed-graph-es-dev.properties -type full -bSize 10000 -cf sct2_Description_Snapshot_INT_20150131.txt >> desc.log 


	java -jar snomed-graph-loader-0.1.1-SNAPSHOT.jar -config snomed-graph-es-dev.properties -type full -bSize 10000 -cf sct2_Relationship_Snapshot_INT_20150131.txt >> rel.log 

---
##Build & Deployment

Locally application can be installed using command 'mvn clean -DskipTests=true package install'. However there are set of ansible scripts which uses jenkins's push to deploy on the server.


##Run

Locally application can be run using command 'mvn -DRefsetConfig=src/main/resources/refset-dev.properties clean tomcat7:run'


---
###Maintenance

Most people do not need this. And only relevant if you are trying to drop existing data & schema

Dropping keyspace. Use cqlsh provided with datastax cassandra distribution.

 
	DROP KEYSPACE snomed;
	
MORE TBD

---
##API Documentation

API documentation is available at [IHTSDO Refset Service API](http://dev-refset.ihtsdotools.org:8080/refset/ "IHTSDO Refset Service API") 





&copy; IHTSDO 

