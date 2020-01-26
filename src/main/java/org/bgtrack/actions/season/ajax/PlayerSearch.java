package org.bgtrack.actions.season.ajax;

import java.util.ArrayList;
import java.util.List;

import org.bgtrack.auth.JsonAction;
import org.bgtrack.models.user.Reguser;
import org.bgtrack.models.user.daos.UserDAO;

public class PlayerSearch extends JsonAction {
	private static final long serialVersionUID = 1L;

	private String username;
		
	private List<PersonSearchResponse> personSearchResponses;

	public static final String PLAYER_SEARCH_PARAM_MISSING = "To search for a player, you must specify a username.";
	
	@Override
	public void validate() {
		
		super.validate();
		
		if (this.username == null || this.username.isEmpty()) {
			createErrorResponse();
		}

	}
	
	@Override
	public boolean isAuthenticationRequired() {
		return true;
	}

	private void createErrorResponse() {
		this.setJsonErrorObject(new JsonError(1, PLAYER_SEARCH_PARAM_MISSING));
		this.addActionError(PLAYER_SEARCH_PARAM_MISSING);
	}
	
	@Override
	public String execute() throws Exception {
		
		List<Reguser> matchedUsers = UserDAO.getUsersByUsername(this.username);
		
		this.personSearchResponses = createListOfPersonSearchResponses(matchedUsers);
		
		return SUCCESS;
		
	}
	
	private ArrayList<PersonSearchResponse> createListOfPersonSearchResponses(List<Reguser> matchedUsers) {
		
		ArrayList<PersonSearchResponse> personSearchResponses = new ArrayList<PersonSearchResponse>();
		
		for (Reguser matchedUser : matchedUsers) {
			
			PersonSearchResponse personSearchResponse = new PersonSearchResponse(matchedUser);
			
			personSearchResponses.add(personSearchResponse);
						
		}
		
		return personSearchResponses;
		
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public List<PersonSearchResponse> getPersonSearchResponses() {
		return personSearchResponses;
	}

	public void setPersonSearchResponses(List<PersonSearchResponse> personSearchResponses) {
		this.personSearchResponses = personSearchResponses;
	}

	public class PersonSearchResponse {
		
		private String username;
		private String userId;
		
		public PersonSearchResponse(Reguser reguser) {
			this.setUsername(reguser.getUsername());
			this.setUserId(reguser.getUserId());
		}
		
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}
		
	}
	
}
