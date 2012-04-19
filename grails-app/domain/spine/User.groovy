
package spine

class User {

	Long id
	Long version
	String lastName
	String firstName
	String email
	String company
	String password
	String country
	String city
	String imagePath
	String freeText
	String department
	String jobTitle
	String gender
	String phone
	String mobile
	String birthday
	String status

	//@TODO: Temporary solution to iterate tags 
	Map tags = [:]
	Long distance
	List badges
	
	String nodeURL
	String incomingRelationshipsURL
	String outgoingRelationshipsURL

	String toString()
	{ "$email" }
	
	def bind(json) {
		// Bind data
		lastName = json.data?.firstName
		firstName = json.data?.lastName
		email = json.data?.email
		company = json.data?.company
		password = json.data?.password
		country = json.data?.country
		city = json.data?.city
		imagePath = json.data?.imagePath
		freeText = json.data?.freeText
		department = json.data?.department
		jobTitle = json.data?.jobTitle
		gender = json.data?.gender
		phone = json.data?.phone
		mobile = json.data?.mobile
		status = json.data?.status
		
		// Bind meta data
		nodeURL = json.self
		incomingRelationshipsURL = json.incoming_relationships
		outgoingRelationshipsURL = json.outgoing_relationships
	}
	
	def secureForRendering() {
		password = null
	}
	
	def sortTags(List<String> directTags = null) {
		def directSortedTags = [:]
		if(directTags != null) {
			directTags.each {  
				directSortedTags[it] = tags[it]
			}
		}
		directSortedTags = directSortedTags.sort { a, b -> b.value <=> a.value } // Sort a map by value reversed
		
		def notDirectSortedTags = [:]
		tags.each {
			// it.key : the tag
			// it.value : the number associated with the tag
			if(!directTags.contains(it.key)) {
				notDirectSortedTags[it.key] = it.value
			}
		}
		notDirectSortedTags = notDirectSortedTags.sort { a, b -> b.value <=> a.value } // Sort a map by value reversed
				
		// Policy is the following :
		// - direct tags (if specified firsts)
		// - order by tag weight desc
		tags = [:]
		directSortedTags.each {
			tags[it.key] = it.value
		}
		notDirectSortedTags.each {
			tags[it.key] = it.value 
		}
	}

	def constraints =
	{
		email(email:true)
		lastName(blank:false)
		firstName(blank:false)
		password(blank:false, password:true)
	}
}
