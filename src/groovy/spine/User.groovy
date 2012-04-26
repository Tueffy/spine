
package spine

class User extends Node {

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
	
	String toString()
	{ "$email" }
	
	def bind(json) {
		
		super.bind(json)
		
		// Bind data
		lastName = properties?.firstName
		firstName = properties?.lastName
		email = properties?.email
		company = properties?.company
		password = properties?.password
		country = properties?.country
		city = properties?.city
		imagePath = properties?.imagePath
		freeText = properties?.freeText
		department = properties?.department
		jobTitle = properties?.jobTitle
		gender = properties?.gender
		phone = properties?.phone
		mobile = properties?.mobile
		status = properties?.status
	}
	
	def persist(GraphCommunicatorService graphCommunicatorService) {
		if(firstName) properties.put('firstName', firstName)
		if(lastName) properties.put('lastName', lastName)
		if(email) properties.put('email', email)
		if(company) properties.put('company', company)
		if(password) properties.put('password', password)
		if(country) properties.put('country', country)
		if(city) properties.put('city', city)
		if(imagePath) properties.put('imagePath', imagePath)
		if(freeText) properties.put('freeText', freeText)
		if(department) properties.put('department', department)
		if(jobTitle) properties.put('jobTitle', jobTitle)
		if(gender) properties.put('gender', gender)
		if(phone) properties.put('phone', phone)
		if(mobile) properties.put('mobile', mobile)
		if(status) properties.put('status', status)
		super.persist(graphCommunicatorService)
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
		// directSortedTags = directSortedTags.sort { a, b -> b.value <=> a.value } // Sort a map by value reversed
		directSortedTags = directSortedTags.sort { it.key } // Sort a map by key
		
		def notDirectSortedTags = [:]
		tags.each {
			// it.key : the tag
			// it.value : the number associated with the tag
			if(!directTags.contains(it.key)) {
				notDirectSortedTags[it.key] = it.value
			}
		}
		// notDirectSortedTags = notDirectSortedTags.sort { a, b -> b.value <=> a.value } // Sort a map by value reversed
		notDirectSortedTags = notDirectSortedTags.sort { it.key } // Sort a map by key
				
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
