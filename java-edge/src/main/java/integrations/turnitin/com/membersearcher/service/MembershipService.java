package integrations.turnitin.com.membersearcher.service;

import java.util.concurrent.CompletableFuture;

import integrations.turnitin.com.membersearcher.client.MembershipBackendClient;
import integrations.turnitin.com.membersearcher.model.Membership;
import integrations.turnitin.com.membersearcher.model.MembershipList;
import integrations.turnitin.com.membersearcher.model.User;
import integrations.turnitin.com.membersearcher.model.UserList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MembershipService {
	@Autowired
	private MembershipBackendClient membershipBackendClient;

	/**
	 * Method to fetch all memberships with their associated user details included.
	 * This method calls out to the php-backend service and fetches all memberships,
	 * it then calls to fetch the all user details and associates them with their
	 * corresponding membership.
	 * 
	 * @return A CompletableFuture containing a fully populated MembershipList
	 *         object.
	 */
	public CompletableFuture<MembershipList> fetchAllMembershipsWithUsers() {
		CompletableFuture<MembershipList> membershipList = membershipBackendClient.fetchMemberships();
		UserList userList = membershipBackendClient.fetchUsers().join();

		return membershipList.thenApply(memberships -> {
			memberships.getMemberships().forEach(membership -> membership
					.setUser(userList.getUsers().stream()
							.filter(user -> user.getId().equals(membership.getUserId()))
							.findFirst().orElse(null)));
			return memberships;
		});
	}

}
