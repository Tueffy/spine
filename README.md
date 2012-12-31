# spine (refactoring)

Things to see are currently in: 

 - grails-app/services/
 - test/
 - src/groovy/

## Services description: 

 - Neo4jService: Client for the Neo4j REST API
 - SpineService: Core logic of spine
 - TestDataImportService: Importation of test data *(not a clean solution, to be changed)*

## Graph Logic:
 
The graph is composed of Nodes and Relationships which are representation by the following classes: 

 - GraphNode
 - GraphRelationship

## Things which still to be changed: 

 - The way the configuraiton is managed is not nice
 - Exceptions need to be improved


## Set up project

 - Grails 2.1
 - Neo4j (Version 1.8)
 - Need to set-up two Neo4j instances: 
   One for running the application, one for running the tests (configure the ports in Config.groovy)