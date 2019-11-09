package org.bgtrack.actions.season.ajax;

import java.util.ArrayList;
import java.util.List;

import org.bgtrack.auth.JsonAction;
import org.bgtrack.models.user.Reguser;
import org.bgtrack.models.user.daos.UserDAO;
import org.bgtrack.utils.BGTConstants;

public class PlayerSearch extends JsonAction {
	private static final long serialVersionUID = 1L;

	private String firstName;
	private String lastName;
		
	private List<PersonSearchResponse> personSearchResponses;

	@Override
	public void validate() {
		
		super.validate();
		
		if (this.isValidationFailed)
			return;
		
		if (this.firstName == null && this.lastName == null) {
			createErrorResponse();
			return;
		}
			
		if (this.firstName.isEmpty() && this.lastName.isEmpty())
			createErrorResponse();
		
	}
	
	@Override
	public boolean isAuthenticationRequired() {
		return true;
	}

	private void createErrorResponse() {
		this.setValidationFailed(true);
		this.setJsonErrorObject(new JsonError(1, BGTConstants.playerSearchParamMissing));
	}
	
	@Override
	public String execute() throws Exception {
		
		if (this.isValidationFailed) {
			return ERROR;
		}
		
		List<Reguser> matchedUsers = UserDAO.getUserByFirstAndLastName(this.firstName, this.lastName);
		
		if (matchedUsers.isEmpty()) {
			// its possible the user put the last name only in the input field
			matchedUsers = UserDAO.getUserByLastName(this.firstName);
		}
		
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

	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public List<PersonSearchResponse> getPersonSearchResponses() {
		return personSearchResponses;
	}

	public void setPersonSearchResponses(List<PersonSearchResponse> personSearchResponses) {
		this.personSearchResponses = personSearchResponses;
	}

	public class PersonSearchResponse {
		
		private String firstName;
		private String lastName;
		private String userId;
		
		public PersonSearchResponse(Reguser reguser) {
			this.setFirstName(reguser.getFirstName());
			this.setLastName(reguser.getLastName());
			this.setUserId(reguser.getUserId());
		}
		
		public String getFirstName() {
			return firstName;
		}
		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}
		
	}
	
}
