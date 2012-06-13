package spine

class FromRelationship extends Relationship {

	def LogEntry start
	def User end
	
	def FromRelationship() {
		type = 'from'
	}
	
	def persist(GraphCommunicatorService graphCommunicatorService) {
		super.start = this.start
		super.end = this.end
	}
	
}
