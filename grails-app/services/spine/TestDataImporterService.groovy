package spine

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
	def List<GraphNode> importUsers(String filename) {
		def List<GraphNode> users = []
		File file = new File(filename);
		
		// Each line is split
		def firstLinePassed = false;
		file.splitEachLine(",") { fields -> 
			if(firstLinePassed) {
				def GraphNode user = new GraphNode()
				user.uuid = fields[0]
				user.firstname = fields[1]
				user.lastname = fields[2]
				user.password = fields[3]
				user.birthdate = fields[4]
				user.city = fields[5]
				user.country = fields[6]
				user.email = fields[7]
				user.salt = fields[8]
				user.password = spineService.hashPassword(user.password, user.salt)
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
	def List<GraphNode> importAndPersistsUsers(String filename) {
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
	def List<GraphRelationship> importRelationships(String filename, List<GraphNode> users) {
		def List<GraphRelationship> relationships = []
		File file = new File(filename);
		
		// Each line is split
		def firstLinePassed = false;
		file.splitEachLine(",") { fields ->
			if(firstLinePassed) {
				def GraphNode startNode = users.find { it.uuid == fields[0] }
				def GraphNode endNode = users.find { it.uuid == fields[1] }
				def GraphRelationship relationship  = new GraphRelationship(startNode, endNode, 'CONNECT')
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
				spineService.tagUser(relationship.startNode, relationship.endNode, tag)
			}
		}
		
		return relationships
	}
	
}
