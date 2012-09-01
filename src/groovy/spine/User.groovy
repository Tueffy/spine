
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
	Long lastNotifications

	//@TODO: Temporary solution to iterate tags 
	Map tags = [:]
	Long distance
	List badges
	
	String toString()
	{ "$email" }
	
	def bind(json) {
		
		super.bind(json)
		
		// Bind data
		lastName = data?.lastName
		firstName = data?.firstName
		email = data?.email
		company = data?.company
		password = data?.password
		country = data?.country
		city = data?.city
		imagePath = data?.image
		freeText = data?.freeText
		department = data?.department
		jobTitle = data?.jobTitle
		gender = data?.gender
		phone = data?.phone
		mobile = data?.mobile
		status = data?.status
		lastNotifications = data?.lastNotifications
	}
	
	def persist(GraphCommunicatorService graphCommunicatorService) {
		if(firstName) data.put('firstName', firstName)
		if(lastName) data.put('lastName', lastName)
		if(email) data.put('email', email)
		if(company) data.put('company', company)
		if(password) data.put('password', password)
		if(country) data.put('country', country)
		if(city) data.put('city', city)
		if(imagePath) data.put('image', imagePath)
		if(freeText) data.put('freeText', freeText)
		if(department) data.put('department', department)
		if(jobTitle) data.put('jobTitle', jobTitle)
		if(gender) data.put('gender', gender)
		if(phone) data.put('phone', phone)
		if(mobile) data.put('mobile', mobile)
		if(status) data.put('status', status)
		if(lastNotifications) data.put('lastNotifications', lastNotifications)
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
	
	def reIndex(SuperIndexService superIndexService) {
		if(!self)
			throw new Exception("Impossible to index a non persisted user!")
			
		if(email) superIndexService.addEmailToIndex(email, self)
		if(firstName) superIndexService.addFirstNameToIndex(firstName, self)
		if(lastName) superIndexService.addLastNameToIndex(lastName, self)
		if(city) superIndexService.addCityToIndex(city, self)
		if(jobTitle) superIndexService.addJobTitleToIndex(jobTitle, self)
		if(department) superIndexService.addDepartmentToIndex(department, self)
		if(phone) superIndexService.addPhoneToIndex(phone, self)
	}
	
	def getIncomingRelationships(GraphCommunicatorService graphCommunicatorService, String tag) {
		def query = ' '
		query += ' start me=node:super_index("email:{email}") '
		query += ' match user-[r:connect]->me '
		if(!tag.trim().isEmpty())
			query += ' where has(r.`{tag}`) '
		query += ' return user '
	}
}
