package spine

class LogEntry extends Node {	def String type	def String action
	def String on
	def long time	def FromRelationship from	def ToRelationship to
	
	def LogEntry() {
		time = System.currentTimeMillis()
	}
	
	def bind(json, fronRelationshipJson = null, toRelationshipJson = null) {
		super.bind(json)
	}
	
	def setFrom(User user) {
		from = new FromRelationship()
		from.start = this
		from.end = user
	}
	
	def setTo(User user) {
		to = new ToRelationship()
		to.start = this
		to.end = user
	}
	
	def persist(GraphCommunicatorService graphCommunicatorService) {
		
		if(type) data.put('type', type)
		if(action) data.put('action', action)
		if(on) data.put('on', on)
		if(time) data.put('time', time)
		
		super.persist(graphCommunicatorService)
		
		if(from != null)
			from.persist(graphCommunicatorService)
		if(to != null)
			to.persist(graphCommunicatorService)
	}
}
