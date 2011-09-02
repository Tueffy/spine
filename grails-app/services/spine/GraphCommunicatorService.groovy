package spine

import grails.converters.JSON
import static groovyx.net.http.ContentType.JSON
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient

class GraphCommunicatorService {

    static transactional = false

	def http = new RESTClient( 'http://localhost:7474' )
	
	def neoPost(requestPath, requestQuery) {
		def result = ''
		println 'Request query = ' + requestQuery
		try {
			http.post( path: requestPath, body: requestQuery, requestContentType: groovyx.net.http.ContentType.JSON    ) {resp, json ->
				result = json }
		} catch (HttpResponseException ex) {
			println 'http exception: ' + ex.toString()
			return []
		}
		return result
	}
	
	def neoGet(String requestPath) {
		//returns json
		def result = ''
		try {
			http.get( path: requestPath,  requestContentType: groovyx.net.http.ContentType.JSON    ) {resp, json ->
				result = json }
		}	catch (HttpResponseException ex) {
			println 'Nothing found when filtering edges: ' + ex.toString()
			return []
		}
		return result
	}
		
	def neoGet(String requestPath, LinkedHashMap requestQuery) {
		//returns json
		def result = ''
		try {
			http.get( path: requestPath , query: requestQuery,  requestContentType: groovyx.net.http.ContentType.JSON    ) {resp, json ->
				result = json }
		}	catch (HttpResponseException ex) {
			println 'Nothing found when filtering edges: ' + ex.toString()
			return []
		}
		return result
	}
	
	def neoPut(requestPath,  requestQuery) {
		def result = ''
		try {
			http.put( path: requestPath, body: requestQuery, requestContentType: groovyx.net.http.ContentType.JSON )
		} catch (HttpResponseException ex) {
			println 'http exception: ' + ex.toString()
			return []
		}
		return result
	}
	
	def neoDelete(requestPath) {
		def result = ''
		try {
			http.delete( path: requestPath, requestContentType: groovyx.net.http.ContentType.JSON )
		} catch (HttpResponseException ex) {
			println 'http exception: ' + ex.toString()
			return []
		}
		return result
	}
}
