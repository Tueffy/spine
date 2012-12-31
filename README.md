spine (refactoring)
================

Services description: 
====

 - Neo4jService: Client for the Neo4j REST API
 - SpineService: Core logic of spine
 - TestDataImportService: Importation of test data *(not a clean solution, to be changed)*


Graph Logic: 
====
The graph is composed of Nodes and Relationships which are representation by the following classes: 

 - GraphNode
 - GraphRelationship

Things which still to be changed: 
====
 - The way the configuraiton is managed is not nice
 - Exceptions need to be improved


