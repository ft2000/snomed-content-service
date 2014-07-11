<<<<<<< HEAD
THIS IS WORK IN PROGRESS YET - AT the moment its more of scratchpad
This project provide a querying interface to http endpoints of sparql 1.1 compliant servers.
I plan to secure this service later and also provide a federated query interface

Some guide lines to use this interface
mvn clean install tomcat7:run

go to http://localhost:/8081

Marmotta web server should be running on port 9090. You can start marmotta server by using marmotta webapp launcher ..launchers/marmotta-webapp
mvn tomcat7:run -Dmarmotta.port=9090 -Dmarmotta.home=/tmp/marmotaa-app 

Fuseki can be run using tdb text config. Full text to work index needs to be created.

./fuseki-server --config=config-tdb-text.ttl


=======
snomed-content-service
======================

Graph based Snomed Content Service
>>>>>>> branch 'master' of https://github.com/IHTSDO/snomed-content-service.git
