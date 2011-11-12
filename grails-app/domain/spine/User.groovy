package spine

class User {

	Long id
	Long version
	String lastname
	String firstname
	String email
	String company
	String password
	String country
	String city
	String imagepath
	String freetext
	String toString()
	{ "$email" }
	def constraints =
	{
		email(email:true)
		lastname(blank:false)
		firstname(blank:false)
		password(blank:false, password:true)
	}
}
