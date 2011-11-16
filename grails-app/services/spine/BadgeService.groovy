package spine

import spine.Badge

class BadgeService {

    static transactional = false

    /**
     * based upon tag list, badges will be returned; at the moment we just hardcode some rules
     * 
     * @return
     */
	def evaluateTags(HashMap taglist) {

		// somehow I cannot get them via Boostrapping...anyway some test badges
		def badge1 = new Badge(name: "ITIL Champ", description: "Champion of ITIL processes", image : "itilchamp.png", ruleset : [[ITIL:5]])			
		def badge2 = new Badge(name: "Java Guru", description: "Guru of Java Development", image : "javaguru.png", ruleset : [[Java:5, Spring:3]])
		def badge3 = new Badge(name: "Frankfurter", description: "Knows about all clubs in Frankfurt", image : "frankfurter.png", ruleset : [[Frankfurt:3]])
		def badge4 = new Badge(name: "Scrum Master", description: "Agile Scrum project manager", image : "scrummaster.png", ruleset : [[Agile:5]])
		def badge5 = new Badge(name: "Innovator", description: "Brings new ideas on the table", image : "innovator.png", ruleset : [[Innovation:5]])
		def badge6 = new Badge(name: "Chinese", description: "Knows in detail how to work with China", image : "chinese.png", ruleset : [[Chinese:3]])
		def badge7 = new Badge(name: "Trading Expert", description: "Expert in several trading products", image : "tradingexpert.png", ruleset : [[Trading:5], [Trading:3, Swaps:3]])
		def badge8 = new Badge(name: "Accounting Master", description: "Knows all about accounting", image : "accountingmaster.png", ruleset : [[Accounting:5]])
		def badge9 = new Badge(name: "The Operator", description: "Master of operational processes", image : "theoperator.png", ruleset : [[Operations:5]])
		def badge10 = new Badge(name: "Cloud Champ", description: "Expert on cloud computing", image : "cloudchamp.png", ruleset : [[Cloud:3]])

		def badgeList = [badge1, badge2, badge3, badge4, badge5, badge6, badge7, badge8, badge9, badge10]
		
		// start with an empty badgeList
		def assignedBadges = []
		def anyRule = false
		def thisRule = true
		
		// loop through the badge list and decide, if badge is applicable; ruleset is a list of hashmaps, one rule is AND, in between rules is OR
		badgeList.each {
			
			// pessimistic approach
			anyRule = false
			
			// loop through rules in ruleset
			it.ruleset.each {
				
				// optimistic approach for this rule
				thisRule = true
				
				// TO DO: replace with a closure if possible
				it.each {
					
					if (!taglist.containsKey(it.key))
						thisRule = false
					else
						if (taglist[it.key] < it.value)
							thisRule = false
				}
				
				if (thisRule)
					anyRule = true
			}
			
			if (anyRule)
				assignedBadges.add(it)
		}

		// return list of assigned badges
		return assignedBadges
    }
}
