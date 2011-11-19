import static org.codehaus.groovy.grails.commons.ConfigurationHolder.getConfig

beans = {
    httpBuilder(groovyx.net.http.HTTPBuilder, config.neo4j.url)
}