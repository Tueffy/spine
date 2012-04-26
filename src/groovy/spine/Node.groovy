package spine

abstract class Node {

	def String self
	def String incomingRelationships
	def String outgoingRelationships
	def String createRelationship
	
	def Map properties = [:]
	
	def bind(json) {
		json.data.each {
			properties.put(it.key, it.value)
		}
		self = json.self
		incomingRelationships = json.incoming_relationships
		outgoingRelationships = json.outgoing_relationships
		createRelationship = json.create_relationship
	}
	
	def persist(GraphCommunicatorService graphCommunicatorService) {
		if(self) // Update node 
		{
			
		}
		else // Create node
		{ 
			
		}
	}
}
