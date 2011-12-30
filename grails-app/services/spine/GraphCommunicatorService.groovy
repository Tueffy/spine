
package spine

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.apache.commons.logging.LogFactory
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.*

class GraphCommunicatorService {

    static transactional = false
    private static final log = LogFactory.getLog(this)

    HTTPBuilder httpBuilder = new HTTPBuilder('http://localhost:7474')

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
        log.trace("Sending Request: ${method.name()} : ${requestPath} : ${requestQuery}")

        return httpBuilder.request(method, groovyx.net.http.ContentType.JSON) {req ->
            uri.path = requestPath

            if (method == GET) {
                uri.query = requestQuery
            } else if (method in [PUT, POST]) {
                body = requestQuery
            }

            response.success = { resp, json ->
                log.trace("Received response: ${json}")
                return json
            }

            response.failure = { resp ->
                log.error("Request failure: ${resp.properties}")
                return []
            }
        }
    }
}
