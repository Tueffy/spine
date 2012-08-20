package spine

import grails.converters.JSON;
import java.util.List;

class TestController {

	def GraphCommunicatorService graphCommunicatorService;
	def NetworkService networkService;
	def SuperIndexService superIndexService;
	def SpineService spineService;
	
	def index = {
		def json = graphCommunicatorService.neoGet('/db/data/index/node/names', ['query': 'email:*'])
		json.each { indexNode(it.self, it) }
		render "ok"
	}
	
	def tags = {
		def tags = networkService.getAllProperties();
		render tags;
	}
	
	def network2 = {
		def User user = new User()
		def tags = [Agile: 1, Java: 3, IT: 5, Spring:1]
		def directTags = ['Java']
		user.tags = tags
		
		render user.tags
		user.sortTags(directTags)
		render user.tags
		
	}
	
	def lucene = {
		render networkService.parseSearchQueryIntoLuceneQuery('Java AND Frankfurt');
	}
	
	def log = {
		def LogFilter logFilter = new LogFilter()
		logFilter.type = 'tag'
		render logFilter.buildCypherQuery()
	}
	

	/**
	 * Insert / update the data about the node in the super index
	 * @param nodeID
	 * @param json
	 * @return
	 */
	def indexNode(String nodeURI, json = null) {
		if(json == null) {
			json = graphCommunicatorService.neoGet(nodeURI)
		}
		
		// Indexing tags of the node
		def tags = []
		def incomingRelationshipsJson = graphCommunicatorService.neoGet(json.incoming_relationships)
		incomingRelationshipsJson.each
		{
			it.data.each {
				if(!tags.contains(it.key))
				{
					tags.add(it.key)
				}
			}
		}
		tags.each { superIndexService.addTagToIndex(it, nodeURI) }
		
		// Indexing the badges
		if(json.data?.email)
		{
			def badges = []
			def user = spineService.getUser(json.data.email)
			badges = spineService.getBadges(user)
			badges.each {
				superIndexService.addBadgeToIndex(it.toString(), nodeURI)
			}
		}
		
		// Index user basic data
		if(json.data?.email)  // adding email
			superIndexService.addEmailToIndex(json.data.email, nodeURI)
		
		if(json.data?.firstName)  // adding first name
			superIndexService.addFirstNameToIndex(json.data.firstName, nodeURI)
		
		if(json.data?.lastName)  // adding last name
			superIndexService.addLastNameToIndex(json.data.lastName, nodeURI)
		
		if(json.data?.city)  // adding city
			superIndexService.addCityToIndex(json.data.city, nodeURI)
	}

	
    def network = {
		
		/*************
		 * 
		 * 	Parameters
		 * 
		 *************/
		String email = "paul.leward@techbank.com"
		int offset = 0
		int limit = 20
		List filter = ["Java"]
		boolean extended_search = true
		
		render "<strong>Params : </strong><br>"
		render "email = " + email + "<br>"
		render "offset = " + offset + "<br>"
		render "limit = " + limit + "<br>"
		render "filter = " + filter + "<br>"
		render "extended_search = " + extended_search + "<br>"
		
		// TODO : If there is not enough result in the user network in case of search, complete with results from the whole network
		// TODO : The function is becomming too complex, must be splitted into more atomic functions
		// TODO : Refectoring - refactor the way search work to make it less complicated and more maintenable and to get more performances
		// TODO : Pagination associated with search seems not to be working (need to propagate the search query)
		
		// Vars initialization
		def resultNodes = []
		def neighbour = [:]
		
		String luceneQuery = ''
		if(!filter.isEmpty()) {
//			println("Filter = ")
//			println(filter.toString())
			for (i in 0..(filter.size() - 1))
			{
				luceneQuery += 	'tag : ' + filter[i] + ' OR ' +
								'badge : ' + filter[i] + ' OR ' +
								'email : ' + filter[i] + ' OR ' +
								'firstname : ' + filter[i] + ' OR ' +
								'lastname : ' + filter[i] + ' OR ' +
								'city : ' + filter[i]
				if(i < filter.size() - 1) luceneQuery += ' OR '
			}
		}
		else {
			luceneQuery = '*:* '
		}
		
		render "<br><strong>Lucene Query</strong><br> "
		render luceneQuery + "<br>"
		
		/*-------------------------------------------------------------
		 *
		 * 	FIRST QUERY : Get results from the User Network
		 *
		 * ----------------------------------------------------------- */
		// Build the query
		def query = 'start ' +
						'n = node:names(email={SP_user}) '
		if(!filter.isEmpty()) {
			query += ", m = node:super_index('" + luceneQuery + "') "
		}
		query += 	'match ' +
						'p = n-[r:connect*1..5]->m ' +
					'where m <> n ' +
					'return ' +
						'distinct m, min(length(p)) ' +
					'order by min(length(p)) ' +
					'skip ' + offset + ' ' +
					'limit ' + limit + ' '
		
		print("\n\n\n" + query + "\n\n\n");
		
		
		// Execute the query
		def cypherPlugin = '/db/data/ext/CypherPlugin/graphdb/execute_query'
		def json = graphCommunicatorService.neoPost(cypherPlugin, '{"query": "'+ query +'", "params": {"SP_user":"' + email + '"}}')
		
		// Get results
		render "<br><strong>Results from the first query</strong><br>"
		render "<ol>"
		json.data.each {
			neighbour = it[0].data
			neighbour['distance'] = it[1]
			resultNodes.add(neighbour)
			render "<li>" + neighbour['email'] + "</li>"
		}
		render "</ol>"
		
		// How many results ?
		
		int firstQueryNbTotalResults = 0;
		query = 'start ' +
					'n = node:names(email={SP_user}) '
		if(!filter.isEmpty()) {
			query += ", m = node:super_index('" + luceneQuery + "') "
		}
		query += 'match ' +
					'p = n-[r:connect*1..5]->m ' +
				'where m <> n ' + 
				'return ' +
					'count(distinct m) as nb '
		json = graphCommunicatorService.neoPost(cypherPlugin, '{"query": "'+ query +'", "params": {"SP_user":"' + email + '"}}')
		if(!json || !json.data || json.data.size() == 0)
			firstQueryNbTotalResults = 0;
		else
			firstQueryNbTotalResults = (int) json.data[0][0]
			
		render "<br>" + "<strong>Count the total results from the first query : </strong>" + "<br>"
		render query + "<br>"
		render "Nb Results First Query = " + firstQueryNbTotalResults + "<br>"
		
		
		
		/*-------------------------------------------------------------
		*
		* 	SECOND QUERY : Get results from the whole Spine Network
		*
		* ----------------------------------------------------------- */
		// We only execute the second query if there no more (or not enough)
		// result to get from the first one.
		// AND the extended_search parameter must be set to true
		if(extended_search && resultNodes.size() < limit && !filter.isEmpty())
		{
			
			render "<br><i>Entering extended search block... </i><br>"
			
			int newOffset = offset - firstQueryNbTotalResults;
			if(newOffset < 0)
				newOffset = 0
			int newLimit = limit - resultNodes.size()
			
			render "<br><strong>New params</strong><br>"
			render "newOffset = " + newOffset + "<br>"
			render "newLimit = " + newLimit + "<br>"
			
			query = 'start ' +
						'n = node:names(email={SP_user}), '
			if(!filter.isEmpty()) {
				query += " m = node:super_index('" + luceneQuery + "') "
			}
			query += 
					'match ' +
						 'n-[r2?:connect*1..5]->m ' +
					'where ' +
						'r2 is null AND m <> n ' +
					'return ' +
						'distinct m, count(*) AS nbResults ' +
					'skip ' + newOffset + ' ' +
					'limit ' + newLimit + ' '
					
			
			log.info(query)
			
			render "<br><strong>Second query</strong><br>"
			render query + "<br>"
			
			// Execute the query
			cypherPlugin = '/db/data/ext/CypherPlugin/graphdb/execute_query'
			json = graphCommunicatorService.neoPost(cypherPlugin, '{"query": "'+ query +'", "params": {"SP_user":"' + email + '"}}')
			
			// Get results
			render "<br><strong>Results from the second query</strong><br>"
			render "<ol>"
			json.data.each {
				neighbour = it[0].data
				neighbour['distance'] = it[1]
//				println "Elem: " + neighbour
				resultNodes.add(neighbour)
				render "<li>" + neighbour['email'] + "</li>"
			}
			render "</ol>"
		}
		
		
		render "<br><strong>Final Results : " + resultNodes.size() + " </strong><br>"
		render "<ol>"
		resultNodes.each {
			render "<li>" + it['email'] + "</li>"
		}
		render "</ol>"
		
		
		render "Looks ok ! ";
		
	}
}
