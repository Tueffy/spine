package spine

import grails.spring.BeanBuilder

/**
 * User: stas
 * Date: 11/17/11
 */
class BeanCtxFactory {
    private def bb = new BeanBuilder()

    def createAppCtx() {
        bb.beans {
            httpBuilder(groovyx.net.http.HTTPBuilder, 'http://localhost:7474')
            graphCommunicatorService(spine.GraphCommunicatorService) {
                httpBuilder = httpBuilder
            }
            networkService(spine.NetworkService) {
                graphCommunicatorService = graphCommunicatorService
            }
            badgeService(spine.BadgeService)
            importDataService(spine.ImportDataService) {
                networkService = networkService
            }
            spineService(spine.SpineService) {
                networkService = networkService
                badgeService = badgeService
            }
        }

        return bb.createApplicationContext()
    }
}
