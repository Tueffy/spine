package spine

/**
 * Log filter class is used to build query for the log index
 * @author Leward
 *
 */
class LogFilter {
	def User from
	def User to
	def String type
	def String action
	def String on
	def long afterTime
	def long beforeTime

	def buildCypherQuery() {
		def luceneQuery = buildLuceneQuery()
		def where = buildWhereClause()
		def query = "start " +
				" n = node:log('" + luceneQuery + "') " +
				buildUFromAndUTo() +
				" match " +
				" n-[:from]->ufrom, " +
				" n-[:to]->uto " +
				" " + where + " " +
				" return n, ufrom, uto "
		return query
	}

	def executeAndParseQuery(String query, GraphCommunicatorService graphCommunicatorService) {
		println query
		def json = graphCommunicatorService.neoPost(graphCommunicatorService.cypherPlugin, '{"query": "'+ query +'", "params": {}}')

		def results = []
		if(json?.data) {
			json.data.each {
				def LogEntry logEntry = new LogEntry()
				logEntry.bind(it[0])
				results.add(logEntry)
			}
		}

		//println "Results = " + results

		return results
	}

	def String buildLuceneQuery() {
		def luceneQuery = ''

		// Check if there is filter to apply
		if(!type && !on)
			return '*:*'

		if(type)
			luceneQuery = addFilterToLuceneQuery(luceneQuery, 'type', type)
		if(action)
			luceneQuery = addFilterToLuceneQuery(luceneQuery, 'action', action)
		if(on)
			luceneQuery = addFilterToLuceneQuery(luceneQuery, 'on', on)

		luceneQuery = '(' + luceneQuery + ')'
		return luceneQuery
	}

	def private String addFilterToLuceneQuery(String luceneQuery, String key, String value) {
		if(!luceneQuery.isEmpty())
			luceneQuery += ' OR '
		luceneQuery += key.toLowerCase() + ' : ' + value.toLowerCase() + ' '
		return luceneQuery
	}

	def private String buildWhereClause() {
		def where = ''
		if(!afterTime && !beforeTime)
			return where

		if(afterTime)
			where += ' time > ' + afterTime
		if(beforeTime) {
			if(!where.isEmpty())
				where += ' AND '
			where += ' time < ' + beforeTime
		}

		return where
	}

	def private String buildUFromAndUTo() {
		if(!from && !to)
			return ''
		def queryPart = '  '
		
		if(from) {
			queryPart += ' , ufrom = node('+ from.getID() +') '
		}
		
		if(to) {
			queryPart += ' , uto = node('+ to.getID() +') '
		}
		
		return queryPart
	}
}
