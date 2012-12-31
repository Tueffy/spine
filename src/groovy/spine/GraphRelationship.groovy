package spine

class GraphRelationship  {

	def Long id
	def GraphNode startNode
	def GraphNode endNode
	def String type
	def data = [:]

	def GraphRelationship(GraphNode startNode, GraphNode endNode, String type) {
		this.startNode = startNode
		this.endNode = endNode
		this.type = type 
	}
	
	def propertyMissing(String name, value) { data[name] = value }
	def propertyMissing(String name) { data[name] }
}
