package spine

import spine.viewmodel.User

/**
 * Imports test data
 */
class TestDataImporterService {

	def SpineService spineService
	
	/**
	 * Import users from a file
	 * 
	 * Example of file content: 
	 * uuid,firstname,lastname,password,birthdate,city,country
	 * 1,Christian,Tueffers,password,1980-01-01,Frankfurt,Germany
	 * 
	 * @param filename
	 * @return
	 */
	def List<User> importUsers(String filename) {
		def List<User> users = []
		File file = new File(filename);
		
		// Each line is split
		def firstLinePassed = false;
		file.splitEachLine(",") { fields -> 
			if(firstLinePassed) {
				def GraphNode userGraphNode = new GraphNode()
				userGraphNode.uuid = fields[0]
				userGraphNode.firstname = fields[1]
				userGraphNode.lastname = fields[2]
				userGraphNode.password = fields[3]
				userGraphNode.birthdate = fields[4]
				userGraphNode.city = fields[5]
				userGraphNode.country = fields[6]
				userGraphNode.email = fields[7]
				userGraphNode.salt = fields[8]
				userGraphNode.password = spineService.hashPassword(userGraphNode.password, userGraphNode.salt)
				
				def User user = new User(userGraphNode)
				users.add(user)
			}
			else
				firstLinePassed = true
		}
		
		return users
	}
	
	/**
	 * Import users from a file and persist them into database
	 * @param filename
	 * @return
	 */
	def List<User> importAndPersistsUsers(String filename) {
		// Import users from CSV
		def users = importUsers(filename)
		
		// Persist the users
		users.each {
			spineService.addUser(it)
		}
		
		return users
	}
	
	/**
	 * Import relationships between users from a file
	 * 
	 * Example of file content: 
	 * uuid1,uuid2,tags (separated by -)
	 * 1,4,Java
	 * 
	 * @param filename
	 * @param users
	 * @return
	 */
	def List<GraphRelationship> importRelationships(String filename, List<User> users) {
		def List<GraphRelationship> relationships = []
		File file = new File(filename);
		
		// Each line is split
		def firstLinePassed = false;
		file.splitEachLine(",") { fields ->
			if(firstLinePassed) {
				def User startUser = users.find { it.graphNode.uuid == fields[0] }
				def User endUser = users.find { it.graphNode.uuid == fields[1] }
				def GraphRelationship relationship  = new GraphRelationship(startUser.graphNode, endUser.graphNode, 'CONNECT')
				def tags = fields[2].split("-")
				tags.each {
					relationship[it] = 1
				}
				relationships.add(relationship)
			}
			else
				firstLinePassed = true
		}
		
		return relationships
	}
	
	/**
	 * Import relationships between users from a file and persist them into database
	 * @param filename
	 * @param users
	 * @return
	 */
	def List<GraphRelationship> importAndPeristRelationships(String filename, List<GraphNode> users) {
		// Import the relationships from the CSV
		def List<GraphRelationship> relationships = importRelationships(filename, users)
		
		// Persist the relationships
		relationships.each {
			def GraphRelationship relationship = it
			relationship.data.each {
				def String tag = it.getKey()
				def startUser = new User(relationship.startNode)
				def endUser = new User(relationship.endNode)
				spineService.tagUser(startUser, endUser, tag)
			}
		}
		
		return relationships
	}
	
}
