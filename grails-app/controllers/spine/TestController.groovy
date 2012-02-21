package spine

class TestController {

	def GraphCommunicatorService graphCommunicatorService;
	def NetworkService networkService;
	def SuperIndexService superIndexService = new SuperIndexService();
	
	def index = {
		superIndexService.indexAll()
//		superIndexService.pouet()
		render "ok"
	}
	
    def oldindex = {
		networkService.reindexRelationships()
		render("ok")
//		def json = graphCommunicatorService.neoGet('/db/data/index/relationship/edges', ['query': '*:*'])
//		def String string = "";
//		json.each {
////			setRelationShipIndex(it.self, it.data)
//			string += it.toString() + "\n \n \n"
//		}
//		render(string)
	}
}
