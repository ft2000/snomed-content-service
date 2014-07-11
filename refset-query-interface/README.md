#Refset Experimental Query Interface

>THIS IS WORK IN PROGRESS at the moment and more of a scratch pad

This project provides a querying interface to any http endpoints of sparql 1.1 compliant servers.
Plan is to wrap this with security so that only authrorized user can query underlying data. There will be an interface for update end points also.
  
To use this - all you need to do is run 

	mvn clean install tomcat7:run

then go to http://localhost:/8081 and configure database

To add new end point - just add new endpoint in relevant blocks of home.jsp

At present it is configured with a sparql end point with a Marmotta web server expected to run on port 9090. 
You can start marmotta server locally by using marmotta webapp launcher available in your marmotta platform - ..launchers/marmotta-webapp

	mvn tomcat7:run -Dmarmotta.port=9090 -Dmarmotta.home=/tmp/marmotaa-app 

obviously you must have loaded data before running marmotta end points and it can be done using kiwi loader. 
Create required database and user using config/database.sql and run below command with required input data.

	java -jar marmotta-loader-kiwi-3.2.1.jar -a out.rdf.zip -B kiwi -b 'http://snomedtools.info/snomed/term/' -C jdbc:postgresql://localhost:5432/snomed?prepareThreshold=3 -U refset -W refset -I -s 
	
##TDB Data loading

Use following command from your JENA_HOME/bin directory. This is only for initial load. It will load given input(out.rdf) to JENA_HOME/snomed directory.If it is not initial load then use tdbloader instead of tdbloader2

	./tdbloader2 --loc ../snomed /tmp/out.rdf

Jena tdb data can be accessed using Fuseki. It can be started using configuration available in config/config-tdb-text.ttl. 


	./fuseki-server --config=config-tdb-text.ttl

Jena full text search queries will only work if a full text index is created. This can be done using above configuration file config-tdb-text.ttl. It already has required configuration. Full indexing can be done using jena.textindexer 

	java -cp fuseki-server.jar jena.textindexer --desc=config-tdb-text.ttl
Above command will start Fuseki end point is running as http://localhost:3030/snomed/query

Now some guidelines to run query
If you are running select query on Marmotta endpoint then select browse as output option and if it is Fuseki end point then select Table.
Construct query select XML-Jena option for Fuseki end point you can also select plain text or json option for any query type if using Fuseki endpoint. Queries are available in resources/sparql folder. These are just demo queries. Real time query can be more complex.



