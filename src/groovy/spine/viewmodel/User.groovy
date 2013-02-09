package spine.viewmodel

import spine.GraphNode

class User {

	def GraphNode graphNode
	def Map<String, Integer> tags = [:]
	
	def User() {
		graphNode = new GraphNode()
	}
	
	def User(GraphNode graphNode) {
		this.graphNode = graphNode
	}
	
	def propertyMissing(String name, value) { graphNode[name] = value }
	def propertyMissing(String name) { graphNode[name] }
	
}
