package spine

class ToRelationship extends Relationship {

	def LogEntry start
	def User end
	
	def ToRelationship() {
		type = 'to'
	}
	
	def persist(GraphCommunicatorService graphCommunicatorService) {
		super.start = this.start
		super.end = this.end
	}
	
}
