package spine

import org.codehaus.groovy.grails.exceptions.InvalidPropertyException;

abstract class Node {

	def String self
	def String incomingRelationships
	def String outgoingRelationships
	def String createRelationship
	def String properties
	
	def Map data = [:]
	
	def bind(json) {
		json.data.each {
			data.put(it.key, it.value)
		}
		self = json.self
		incomingRelationships = json.incoming_relationships
		outgoingRelationships = json.outgoing_relationships
		createRelationship = json.create_relationship
		properties = json.properties
	}
	
	def persist(GraphCommunicatorService graphCommunicatorService) {
		def String jsonData = graphCommunicatorService.encodeMapToJSONString(data)
		if(self) // Update node 
		{
			graphCommunicatorService.neoPut(properties, jsonData)
		}
		else // Create node
		{ 
			def json = graphCommunicatorService.neoPost('/db/data/node', jsonData)
			bind(json)
		}
	}
	
	def delete(GraphCommunicatorService graphCommunicatorService) {
		if(self) // Node is actually in DB
		{
			graphCommunicatorService.neoDelete(self)
			self = null
		}
		else
			throw new InvalidPropertyException("self property of the node is empty ! ")
	}
}
