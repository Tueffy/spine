
package spine

import groovyx.net.http.AsyncHTTPBuilder
import groovyx.net.http.Method
import org.apache.commons.logging.LogFactory
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.*

import java.util.concurrent.Future;
import java.util.regex.Matcher
import java.util.regex.Pattern


class GraphCommunicatorService {

    static transactional = false
    private static final log = LogFactory.getLog(this)
	String cypherPlugin = '/db/data/ext/CypherPlugin/graphdb/execute_query'

	AsyncHTTPBuilder asyncHTTPBuilder = new AsyncHTTPBuilder(
		poolsize: 10, 
		uri: 'http://localhost:7474', 
		contentType: JSON
	);
	

	def String encodeMapToJSONString(Map map) {
		def jsonString = '{ '
		
		def i = 1;
		def mapSize = map.size()
		map.each {
			jsonString += '"'+ it.key.toString() + '" : '
			if(it.value instanceof Map)
				jsonString += encodeMapToJSONString(it.value)
			else 
				jsonString += '"' + it.value.toString() + '"'
			if(i != mapSize)
				jsonString += ', '
			i++
		}
		
		jsonString += ' }'
		return jsonString
	}
	
    def neoPost(requestPath, requestQuery) {
        internalRequest(POST, requestPath, requestQuery)
    }

    def neoGet(String requestPath) {
        internalRequest(GET, requestPath)
    }

    def neoGet(String requestPath, LinkedHashMap requestQuery) {
        internalRequest(GET, requestPath, requestQuery)
    }

    def neoPut(requestPath, requestQuery) {
        internalRequest(PUT, requestPath, requestQuery)
    }

    def neoDelete(requestPath) {
        internalRequest(DELETE, requestPath)
    }

    private def internalRequest(Method method, String requestPath, requestQuery = null) {
		requestPath = cleanRequestPath(requestPath)
//		log.trace("Sending Request: ${method.name()} : ${requestPath} : ${requestQuery}")
		
		Future result = asyncHTTPBuilder.request(method) {
			uri.path = requestPath
			if(method == GET)
				uri.query = requestQuery
			else if(method in [PUT, POST])
				body = requestQuery
				
			response.success = { resp, json ->
//                log.trace("Received response: ${json}")
                return json
            }

            response.failure = { resp ->
                log.error("Request failure: ${resp.properties}")
                return []
            }
		}
		return result.get();
    }
	
	private String cleanRequestPath(String requestPath) {
		// If the URL starts with http://localhost:7474 remove it
		def Pattern pattern = Pattern.compile("^http://localhost:7474(.*)")
		def Matcher matcher = pattern.matcher(requestPath)
		if(matcher.matches())
			requestPath = matcher.group(1)
		return requestPath
	}
}
