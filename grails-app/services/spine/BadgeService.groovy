
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
		def badge1 = new Badge(name: "Accounting Master", description: "Knows all about accounting", image : "badge_accounting_master.png", ruleset : [[Accounting:3]])
		def badge2 = new Badge(name: "BI Guru", description: "Business Intelligence is her/his business", image : "badge_bi_guru.png", ruleset : [[BI:3]])
		def badge3 = new Badge(name: "Bonds Expert", description: "Expert in trading bonds", image : "badge_bonds_expert.png", ruleset : [[Bonds:3]])
		def badge4 = new Badge(name: "Connoisseur", description: "Loves the good food and wine", image : "badge_connoisseur.png", ruleset : [[Wine:3], [Coffee:3], [Chocolate:3]])
		def badge5 = new Badge(name: "FX Master", description: "Dealing with currencies and ofrein exchanges", image : "badge_fx_master.png", ruleset : [[FX:3]])
		def badge6 = new Badge(name: "Golf Tiger", description: "Follows the track to become the next tiger", image : "badge_golf_tiger.png", ruleset : [[Golf:3]])
		def badge7 = new Badge(name: "Innovator", description: "Brings new ideas on the table", image : "badge_innovator.png", ruleset : [[Innovation:4]])
		def badge8 = new Badge(name: "ITIL Champ", description: "Champion of ITIL processes", image : "badge_itil_champ.png", ruleset : [[ITIL:3]])
		def badge9 = new Badge(name: "Java Guru", description: "Guru of Java Development", image : "badge_java_guru.png", ruleset : [[Java:3]])
		def badge10 = new Badge(name: "Legal Expert", description: "Knows all law tricks", image : "badge_legal_expert.png", ruleset : [[Law:3]])
		def badge11 = new Badge(name: "Sales King", description: "Can sell everything like a used car", image : "badge_sales_king.png", ruleset : [[Sales:3]])
		def badge12 = new Badge(name: "Security Officer", description: "You cannot fool her/him", image : "badge_security_officer.png", ruleset : [[Security:3]])
		def badge13 = new Badge(name: "Shopaholic", description: "Cannot pass any window", image : "badge_shopaholic.png", ruleset : [[Shopping:3]])
		def badge14 = new Badge(name: "Trading Expert", description: "Expert in several trading products", image : "badge_trading_expert.png", ruleset : [[Trading:3], [Trading:2, Swaps:2]])
		def badge15 = new Badge(name: "Web Developer", description: "Develops everything so quickly and fancy", image : "badge_web_develper.png", ruleset : [[HTML:3]])

		def badgeList = [badge1, badge2, badge3, badge4, badge5, badge6, badge7, badge8, badge9, badge10, badge11, badge12, badge13, badge14, badge15]
		
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
