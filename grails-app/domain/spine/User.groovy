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
