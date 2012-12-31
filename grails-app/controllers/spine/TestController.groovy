package spine

import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.*

class TestController {

	def Neo4jService neo4jService

	def index() {
		render "ok"
	}
}
