package spine

import grails.converters.JSON;

import org.codehaus.groovy.grails.exceptions.InvalidPropertyException;

abstract class Relationship {

	def String self
	def String type
	def Node start
	def Node end
	def String properties
	def Map data = [:] // represents the properties
	
	def bind(json) {
		self = json.self
		type = json.type
		properties = json.properties
		data = [:]
		json.data.each {
			data.put(it.key, it.value)
		}
	}
	
	def hasProperty(String property) {
		return data.containsKey(property)
	}
	
	def addProperty(String property, value) {
		if(!hasProperty(property))
			data.put(property, value)
	}
	
	def removeProperty(String property) {
		if(hasProperty(property))
			data.remove(property)
	}
	
	def persist(GraphCommunicatorService graphCommunicatorService) {
		if(start?.self && end?.self) {
			if(self) // update relationship 
			{
				graphCommunicatorService.neoPut(properties, graphCommunicatorService.encodeMapToJSONString(data))
			}
			else // create relationship 
			{
				def props = [ 'to': end.self, 'type': type, 'data': data ]
				def json = graphCommunicatorService.neoPost(start.createRelationship, graphCommunicatorService.encodeMapToJSONString(props))
			}
		}
		else
			throw new InvalidPropertyException("start or end of the relationship is empty ! ")
	}
	
	def delete(GraphCommunicatorService graphCommunicatorService) {
		if(self) // Property is actually in DB
		{
			graphCommunicatorService.neoDelete(self)
			self = null
		}
		else
			throw new InvalidPropertyException("self property of the relationship is empty ! ")
	}
	
}
