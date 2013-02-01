package spine.viewModel

import spine.GraphNode;

class UserNetwork {

	/**
	 * The user at the center of the network
	 */
	GraphNode user
	
	/**
	 * The page to select
	 */
	int page
	
	/**
	 * Items to view per page
	 */
	int itemsPerPage
	
	/**
	 * Total size of the network if it would not be paginated
	 */
	int networkSize
	
	/**
	 * Filter string to filter the network (= search)
	 */
	String filter
	
	/**
	 * List of NetworkedUser in the viewed page
	 */
	List<NetworkedUser> networkedUsers = []
	
	/**
	 * @param user
	 */
	def UserNetwork(GraphNode user, int page = 1, int itemPerPage = 10, String filter = null) {
		this.user = user
		this.page = page
		this.itemsPerPage = itemPerPage
	}
	
	/**
	 * 
	 * @return The current offset of the network pagination (based on the 
	 * current page and the number of items per page). 
	 */
	def int getOffset() {
		return (page - 1) * itemsPerPage
	}
	
}
