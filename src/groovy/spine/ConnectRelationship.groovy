package spine

class ConnectRelationship extends Relationship {
	
	def ConnectRelationship() {
		super()
		type = 'connect'
	}
			
	def addTag(String tag) {
		addProperty(tag, System.currentTimeMillis())
	}
	
	def removeTag(String tag) {
		removeProperty(tag)
	}
	
	def boolean hasTag(String tag) {
		return hasProperty(tag)
	}
		
    static constraints = {
    }
	
	@Override
	public persist(GraphCommunicatorService graphCommunicatorService) {
		super.persist(graphCommunicatorService);
	}
}
