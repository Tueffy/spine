package spine

class StatisticService {

    static transactional = true
	def File searchLog
	def File autoCompleteLog

	def initLogDir = {
		def logDir = new File('logs');
		if(!logDir.exists()) {
			logDir.mkdirs();
		}
	}
	
    def initSearchLog() {
		def today = new Date().calendarDate
		def fileName = "logs/search.${today.year}-${today.month}-${today.dayOfMonth}.log"
		
		if(searchLog == null || !searchLog.name.equals(fileName)) {
			initLogDir()
			searchLog = new File("${fileName}")
			searchLog.createNewFile()
		}
	}
	
	def logSearch(search, nbResults) {
		initSearchLog()
		def today = new Date().calendarDate
		searchLog << ("${today.year}-${today.month}-${today.dayOfMonth} ${today.hours}:${today.minutes}:${today.seconds};${search.toString()};${nbResults.toString()}\n")
	}
	
	def initAutoCompleteLog() {
		def today = new Date().calendarDate
		def fileName = "logs/autoComplete.${today.year}-${today.month}-${today.dayOfMonth}.log"
		
		if(autoCompleteLog == null || !autoCompleteLog.name.equals(fileName)) {
			initLogDir()
			autoCompleteLog = new File("${fileName}")
			autoCompleteLog.createNewFile()
		}
	}
	
	def logAutocomplete(text, nbResults) {
		initAutoCompleteLog()
		def today = new Date().calendarDate
		autoCompleteLog << ("${today.year}-${today.month}-${today.dayOfMonth} ${today.hours}:${today.minutes}:${today.seconds};${text.toString()};${nbResults.toString()}\n")
	}
	
}
