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
	//@TODO: Temporary solution to iterate tags 
	HashMap tags
	Long distance
	List badges

	String toString()
	{ "$email" }

	def constraints =
	{
		email(email:true)
		lastName(blank:false)
		firstName(blank:false)
		password(blank:false, password:true)
	}
}
