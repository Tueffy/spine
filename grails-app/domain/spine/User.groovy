
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
	HashMap tags
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

	def constraints =
	{
		email(email:true)
		lastName(blank:false)
		firstName(blank:false)
		password(blank:false, password:true)
	}
}
