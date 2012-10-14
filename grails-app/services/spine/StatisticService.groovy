package spine

class StatisticService {

    static transactional = true
	def File searchLog
	def File autoCompleteLog

    def initSearchLog() {
		def today = new Date().calendarDate
		def fileName = "search.${today.year}-${today.month}-${today.dayOfMonth}.log"
		
		if(searchLog == null || !searchLog.name.equals(fileName)) {
			searchLog = new File("logs/${fileName}")
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
		def fileName = "autoComplete.${today.year}-${today.month}-${today.dayOfMonth}.log"
		
		if(autoCompleteLog == null || !autoCompleteLog.name.equals(fileName)) {
			autoCompleteLog = new File("logs/${fileName}")
			autoCompleteLog.createNewFile()
		}
	}
	
	def logAutocomplete(text, nbResults) {
		initAutoCompleteLog()
		def today = new Date().calendarDate
		autoCompleteLog << ("${today.year}-${today.month}-${today.dayOfMonth} ${today.hours}:${today.minutes}:${today.seconds};${text.toString()};${nbResults.toString()}\n")
	}
	
}
