package spine

import java.security.MessageDigest;

import net.sf.json.JSONNull;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.InitializingBean;

import spine.exception.AuthenticationException
import spine.exception.MissingMandatoryProperties;
import spine.exception.graphdb.RelationshipNotFoundException;
import spine.viewModel.NetworkedUser
import spine.viewModel.UserNetwork;

class SpineService implements InitializingBean {

    static transactional = false
	def Neo4jService neo4jService
	
	def userIndex = "user_index" // TODO: Move this to config
	
	def void afterPropertiesSet() throws Exception {
		// Ensure that the user index is there
		neo4jService.createNodeIndexIfNotExisting(userIndex)
	}
	
	
	/*
	 * User management
	 */
	
	/**
	 * Returns a node or throws an exception according to a given pair of email / password 
	 * @param email
	 * @param password
	 * @return
	 */
	def GraphNode authenticateUser(String email, String password) {
		def GraphNode user = getUserByEmail(email)
		String hashedPassword = hashPassword(password, user.salt)
		if(!hashedPassword.equals(user.password))
			throw new AuthenticationException("Password and email do not match")
		else 
			return user
	}
	
	/**
	 * Hash algorithm used to protect the passwords
	 * @param password
	 * @return
	 */
	def String hashPassword(String password, String salt) {
		def digest = MessageDigest.getInstance("SHA-512")
		String saltedPassword = password + salt
		digest.update(saltedPassword.getBytes())
		return new BigInteger(1,digest.digest()).toString(16).padLeft(32, '0').toString()
		
	}
	
	/**
	 * 
	 * @return
	 */
	def String generateSalt() {
		String randomString = RandomStringUtils.random(6, true, true)
		return randomString
	}
	
	/**
	 * Get a user with its id
	 * @param id
	 * @return
	 */
	def GraphNode getUserById(Long id) {
		return neo4jService.getNode(id)
	}
	
	/**
	 * Get a user with its email address
	 * @param email
	 * @return
	 */
	def GraphNode getUserByEmail(String email) {
		def nodes = neo4jService.findNode("email", email, userIndex)
		if(nodes.size() == 0)
			return null
		else if(nodes.size() > 1)
			throw new Exception("There is more than one entry with the email '${email}' in the index ${userIndex}")
		else 
			return nodes[0]
	}
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	def addUser(GraphNode user) {
		checkMandatoryFiledsForUser(user)
		neo4jService.persistNode(user)
		reindexUser(user)
	}
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	def updateUser(GraphNode user) {
		checkMandatoryFiledsForUser(user)
		neo4jService.persistNode(user)
		reindexUser(user)
	}
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	def reindexUser(GraphNode user) {
		neo4jService.addNodeToIndex(user, userIndex, "email", user.email, true)
	}
	
	/**
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	def protected checkMandatoryFiledsForUser(GraphNode user) throws Exception {
		if(!user.data.containsKey("password"))
			throw new Exception("Missing property 'password' on user")
		if(!user.data.containsKey("email"))
			throw new Exception("Missing property 'email' on user")
	}
	
	/**
	 *
	 * @param user
	 * @return
	 */
	def refreshUser(GraphNode user) {
		def GraphNode refreshedUser
		if(user.id)
			refreshedUser = getUserById(user.id)
		else if(user.email)
			refreshedUser =  getUserByEmail(user.email)
		else
			throw new Exception("Impossible to refresh an user if none of user.email or user.id are specified. ")
			
		refreshedUser.data.each {
			user.data[it.getKey()] = it.getValue()
		}
	}
	
	
	/*
	 * Tags Management
	 */
	
	/**
	 * Format a tag to be compliant with our formatting rules. 
	 * If normalizeForIndex is set to true, the result will be lower cased. 
	 * @param tag
	 * @return
	 */
	def String normalizeTag(String tag, Boolean normalizeForIndex = false) {
		def previousWasSpace = false
		def String newTag = ""
		for(c in tag) {
			// Upper case char after a replaced underscore
			if(previousWasSpace) {
				// c = c.toUpperCase()
				previousWasSpace = false
			}
				
			// Replace a space by underscore
			if(c == " ") {
				c = "_"
				previousWasSpace = true
			}
				
			newTag += c
		}
		
		if(normalizeForIndex)
			newTag = newTag.toLowerCase()
		
		return newTag
	}
	
	/**
	 * Tag an user and add fill in the database index. 
	 * @param currentUser
	 * @param user
	 * @param tag
	 * @return
	 */
	def tagUser(GraphNode currentUser, GraphNode user, String tag) {
		def GraphRelationship relationship = null
		try {
			relationship = neo4jService.getSingleRelationshipBetween(currentUser, user, "CONNECT")
		} 
		catch (RelationshipNotFoundException e) {
			relationship = new GraphRelationship(currentUser, user, "CONNECT")
			neo4jService.persistRelationship(relationship)
		}
		
		// If the relationship doesn't already have the tag, add it
		tag = normalizeTag(tag)
		if(!relationship.data.hasProperty(tag)) {
			relationship[tag] = 1
			neo4jService.persistRelationshipProperty(relationship, tag, "1")
			def normalizedTagForIndex = normalizeTag(tag, true)
			neo4jService.addNodeToIndex(user, userIndex, "tag", normalizedTagForIndex, true) // Overwrite the previous index entry if there is one
		}
	}
	
	/**
	 * Remove all occurrences of a tag applied to a user
	 * @param currentUser
	 * @param user
	 * @return
	 */
	def untagUser(GraphNode currentUser, GraphNode user, String tag) {
		def GraphRelationship relationship = neo4jService.getSingleRelationshipBetween(currentUser, user, "CONNECT")
		tag = normalizeTag(tag)
		
		// If the tag is not on the relationship: do nothing
		if(relationship == null || !relationship.data.containsKey(tag))
			return
			
		relationship.data.remove(tag)
		// If there is no more tag on the relationship, delete it
		if(relationship.data.size() == 0)
			neo4jService.deleteRelationship(relationship)
		else
			neo4jService.deleteRelationshipProperty(relationship, tag)
			
		// Should we remove the tag from the index? 
		// Yes if the user has no more incomming relationship with this tag
		def userTags = summarizeUserTags(user)
		if(!userTags.containsKey(tag)) {
			def normalizedTagForIndex = normalizeTag(tag, true)
			neo4jService.removeNodeFromIndex(user, userIndex, "tag", normalizedTagForIndex)
		}
	}
	
	/**
	 * Summarize user incoming relationships into a map: 
	 * key is the tag, value the number of occurences of this tag
	 * @param user
	 * @return
	 */
	def Map<String, Long> summarizeUserTags(GraphNode user)
	{
		def List<GraphRelationship> incomingRelationships = neo4jService.getIncomingRelationships(user, "CONNECT")
		def tags = [:]
		incomingRelationships.each {
			it.data.each {
				def tag = normalizeTag(it.getKey())
				if(tags.containsKey(tag))
					tags[tag]++
				else
					tags[tag] = 1
			}
		}
		return tags
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	def List<GraphNode> searchUserIndex(String key, String value) {
		return neo4jService.findNode(key, value, userIndex)
	}
	
	/**
	 * 
	 * @param tag
	 * @return
	 */
	def List<GraphNode> searchTagInUserIndex(String tag) {
		tag = normalizeTag(tag, true)
		return searchUserIndex("tag", tag) 
	}
	
	/**
	 * 
	 * @param query
	 * @return
	 */
	def List<GraphNode> searchUserIndexByLuceneQuery(String query) {
		return neo4jService.findNodeByLuceneQuery(query, userIndex)
	}
	
	/*
	 * User Network
	 */
	
	/**
	 * Get the network of an user with many possible configurations
	 * TODO: Tags need to be tested
	 * @param user
	 * @param offset
	 * @param limit
	 * @param filter
	 * @param extendedSearch
	 * @return
	 */
	def UserNetwork getUserNetwork(GraphNode user, int page = 1, int itemsPerPage = 10, String filter = null, extendedSearch = true) {
		def userNetwork = new UserNetwork(user, page, itemsPerPage, filter)
		
		// Get people within the user network
		queryDirectUserNetwork(userNetwork)
		computeDirectUserNetworkSize(userNetwork)
			
		// If the network is paginated over the number of actual result, 
		// we look for results which the user currently has no connection with. 
		// Do that only if the extended search is enabled
		if(!extendedSearch || userNetwork.networkedUsers.size() >= itemsPerPage)
			return userNetwork
			
		queryUserExtendedNetwork(userNetwork)
		
		return userNetwork
	}
	
	/**
	 * @param userNetwork
	 */
	def protected void queryDirectUserNetwork(UserNetwork userNetwork) {
		// Turns the filter into a proper Lucene query
		def String luceneQuery = null
		if(userNetwork.filter != null)
			luceneQuery = parseSearchIntoLuceneQuery(userNetwork.filter)
			
		def cypherQuery = """
			START 
				n = node(${userNetwork.user.id}) 
				${ (luceneQuery) ? ", m = node:user_index(\""+ luceneQuery +"\")" : "" }
			MATCH 
				${ (luceneQuery) ? "" : "n-[:CONNECT*1..5]->m, " } 
				p = shortestPath(n-[:CONNECT*..5]->m)
			WHERE 
				n <> m
			RETURN 
				DISTINCT m, length(p), relationships(p)
			ORDER BY 
				length(p)
			SKIP ${userNetwork.getOffset()}
			LIMIT ${userNetwork.itemsPerPage}
		"""
				
		def result = neo4jService.doCypherQuery(cypherQuery)
		result.data.each {
			// Content of the "it" variable: 
			// it[0] => m | it[1] => length(p) | it[2] => relationships(p)
			def networkedUser = new NetworkedUser()
			networkedUser.contextUser = userNetwork.user
			networkedUser.user = neo4jService.bindNode(it[0])
			networkedUser.distance = it[1]
			
			if(networkedUser.distance == 1) {
				def tags = it[2].last().data
				tags.each {
					def tag = it.key
					if(!networkedUser.tags.contains(tag)) {
						networkedUser.tags.add(tag)
					}
				}
			}
			
			userNetwork.networkedUsers.add(networkedUser)
		}
	}
	
	/**
	 * @param userNetwork
	 * @return
	 */
	def protected computeDirectUserNetworkSize(UserNetwork userNetwork) {
		// Turns the filter into a proper Lucene query
		def String luceneQuery = null
		if(userNetwork.filter != null)
			luceneQuery = parseSearchIntoLuceneQuery(userNetwork.filter)
		
		def cypherQuery = """
			START 
				n = node(${userNetwork.user.id}) 
				${ (luceneQuery) ? ", m = node:user_index(\""+ luceneQuery +"\")" : "" }
			MATCH 
				${ (luceneQuery) ? "" : "n-[:CONNECT*1..5]->m, " } 
				p = shortestPath(n-[:CONNECT*..5]->m)
			WHERE 
				n <> m
			RETURN 
				COUNT(DISTINCT m) AS nb
		"""
		def result = neo4jService.doCypherQuery(cypherQuery)
		userNetwork.networkSize = (int) result.data[0][0]
	}
	
	/**
	 * @param userNetwork
	 * @return
	 */
	def protected queryUserExtendedNetwork(UserNetwork userNetwork) {
		// Turns the filter into a proper Lucene query
		def String luceneQuery = "email:*"
		if(userNetwork.filter != null)
			luceneQuery = parseSearchIntoLuceneQuery(userNetwork.filter)
		
		int offset = userNetwork.getOffset() - userNetwork.networkSize
		if(offset < 0) offset = 0
		int limit = userNetwork.itemsPerPage - userNetwork.networkedUsers.size()
		
		def cypherQuery = """
			START 
				n = node(${userNetwork.user.id}) 
				, m = node:user_index("${luceneQuery}") 
			MATCH 
				n-[r?:CONNECT*1..5]->m
			WHERE 
				r IS NULL 
				 AND n <> m
				 AND m IS NOT NULL
			RETURN 
				DISTINCT m
			SKIP ${offset}
			LIMIT ${limit}
		"""
		
		def result = neo4jService.doCypherQuery(cypherQuery)
		result.data.each {
			// If query gives no result there will be a null entry, 
			// and we don't want to iterate over it
			def networkedUser = new NetworkedUser()
			networkedUser.contextUser = userNetwork.user
			networkedUser.user = neo4jService.bindNode(it[0])
			networkedUser.distance = 0
			userNetwork.networkedUsers.add(networkedUser)
		}
	}
	
	/**
	 * 
	 * @param startUser
	 * @param endUser
	 * @throws RelationshipNotFoundException
	 * @return
	 */
	def GraphRelationship getDirectConnectionBetweenUsers(GraphNode startUser, GraphNode endUser) throws RelationshipNotFoundException {
		return neo4jService.getSingleRelationshipBetween(startUser, endUser, "CONNECT")
	}

		
	
	/*
	 * Helpers
	 */
	
	/**
	 * TODO: To refactor (code from old spine)
	 * @param filter
	 * @return
	 */
	def String parseSearchIntoLuceneQuery(String filter) {
		def List<String> tokenizedQuery = filter.tokenize(" ")
		def operators = ['AND', 'OR']
		def luceneQuery = ''
		def lastWordWasOperator = false
		tokenizedQuery.each {
			if(lastWordWasOperator || (!lastWordWasOperator && !operators.contains(it)))
			{
				// Default implicit operator :
				if(!lastWordWasOperator && !operators.contains(it) && luceneQuery != '')
					luceneQuery += ' OR '
				
				luceneQuery += '(' +
					'tag : ' + it.toLowerCase() + ' OR ' +
					'badge : ' + it.toLowerCase() + ' OR ' +
					'email : ' + it.toLowerCase() + ' OR ' +
					'firstname : ' + it.toLowerCase() + ' OR ' +
					'lastname : ' + it.toLowerCase() + ' OR ' +
					'city : ' + it.toLowerCase() + '' +
					')'
				lastWordWasOperator = false
			}
			else
			{
				luceneQuery += ' ' + it + ' '
				lastWordWasOperator = true
			}
		}
		return luceneQuery
	}
}
