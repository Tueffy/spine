package spine

class GraphNode {

	def id
	def data = [:]
	
	def propertyMissing(String name, value) { data[name] = value }
	def propertyMissing(String name) { data[name] }
	
}
