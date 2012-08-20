package spine

class LogService {

	static transactional = true
	def NetworkService networkService
	static logIndexPath = '/db/data/index/node/log/'

	def addTag(String tag, User from, User to) {
		LogEntry logEntry = new LogEntry()
		logEntry.type = 'tag'
		logEntry.action = 'add'
		logEntry.on = tag
		logEntry.setFrom(from)
		logEntry.setTo(to)
		logEntry.persist(networkService.graphCommunicatorService)
		addToLogIndex(logEntry)
	}

	def removeTag(String tag, User from, User to) {
		LogEntry logEntry = new LogEntry()
		logEntry.type = 'tag'
		logEntry.action = 'remove'
		logEntry.on = tag
		logEntry.setFrom(from)
		logEntry.setTo(to)
		logEntry.persist(networkService.graphCommunicatorService)
		addToLogIndex(logEntry)
	}

	def addToLogIndex(LogEntry logEntry) {
		if(!logEntry.self)
			throw new Exception("You can not index a not persisted LogEntry object")
		insertIntoLogIndex("type", logEntry.type, logEntry.self)
		insertIntoLogIndex("action", logEntry.action, logEntry.self)
		insertIntoLogIndex("on", logEntry.on, logEntry.self)
		insertIntoLogIndex("from", networkService.getIdFromURI(logEntry.from.end.self), logEntry.self)
		insertIntoLogIndex("to", networkService.getIdFromURI(logEntry.to.end.self), logEntry.self)
	}
	
	def private insertIntoLogIndex(String key, String value, String nodeURI) {
		def requestQuery = [
			'key' : key,
			'value' : value.toLowerCase(),
			'uri' : nodeURI ]
		networkService.graphCommunicatorService.neoPost(logIndexPath, requestQuery)
	}

	def getNotifications(LogFilter logFilter) {
		def query = logFilter.buildCypherQuery()
	}
	
	def getUserNotifications(User user) {
		def LogFilter logFilter = new LogFilter()
		
		if(user?.lastNotifications)
			logFilter.afterTime = user.lastNotifications
		else 
			logFilter.afterTime = 0
		
		logFilter.to = user
		
		def query = logFilter.buildCypherQuery()
		def results = []
		results = logFilter.executeAndParseQuery(query, networkService.graphCommunicatorService)
		return results
	}
}
