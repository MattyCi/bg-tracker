package org.bgtrack.actions.season.ajax;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bgtrack.auth.JsonAction;
import org.bgtrack.auth.ShiroDeleteAction;
import org.bgtrack.auth.UserBuilder;
import org.bgtrack.auth.UserBuilder.UserBuilderException;
import org.bgtrack.models.user.Reguser;
import org.bgtrack.models.user.daos.UserDAO;
import org.bgtrack.utils.BGTConstants;
import org.bgtrack.utils.HibernateUtil;

public class GuestPlayerCreate extends JsonAction {
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = LogManager.getLogger(GuestPlayerCreate.class);

	private String firstName;
	private String lastName;
		
	GuestPlayerCreateResponse guestPlayerCreateResponse = null;

	private static final String PLAYER_CREATE_PARAM_MISSING = "Please provide a first and last name for the new player."; 

	@Override
	public boolean isAuthenticationRequired() {
		return true;
	}
	
	@Override
	public Boolean isCsrfProtected() {
		return true;
	}
	
	@Override
	public void validate() {
		
		super.validate();
		
		if (this.hasActionErrors())
			return;
		
		if (this.firstName == null || this.lastName == null) {
			createValidationErrorResponse();
			return;
		}
			
		if (this.firstName.isEmpty() || this.lastName.isEmpty())
			createValidationErrorResponse();
		
	}

	private void createValidationErrorResponse() {
		this.setJsonErrorObject(new JsonError(1, PLAYER_CREATE_PARAM_MISSING));
		this.addActionError(PLAYER_CREATE_PARAM_MISSING);
	}
	
	@Override
	public String execute() throws Exception {
		
		UserBuilder userBuilder = new UserBuilder(new Reguser());
		
		String randomPass = userBuilder.generateRandomPassword();
		
		try {
			
			userBuilder.buildFirstName(firstName);
			
			userBuilder.buildLastName(lastName);
			
			userBuilder.buildPassword(randomPass, randomPass);
			
			String currentTimestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			
			String guestUsername = currentTimestamp + "@seasongg.com";
			
			userBuilder.buildUsername(guestUsername);
			
			userBuilder.createGuestAccountRedeemToken();
			
			userBuilder.setCreatorForGuestAccountRedeemToken(this.regUser);
			
		} catch(UserBuilderException e) {
			
			LOG.info("User {} tried creating a guest user but the following error occured: {}", shiroUser.getPrincipal(),
					e.getMessage());
			
			addActionError(e.getMessage());
			this.setJsonErrorObject(new JsonError(1, e.getMessage()));
			return ERROR;
			
		} catch(Exception e) {
			
			LOG.error("User {} tried creating a guest user but the following unexpected error occured: {}", shiroUser.getPrincipal(),
					e.getMessage());
			
			this.setJsonErrorObject(new JsonError(2, GENERIC_ERROR));
			return ERROR;
		}
		
		HibernateUtil.persistObject(userBuilder.getReguser());
		
		userBuilder.getReguser().getAccountRedeemToken().setUserId(userBuilder.getReguser().getUserId());
		
		HibernateUtil.persistObject(userBuilder.getReguser().getAccountRedeemToken());
		
		GuestPlayerCreateResponse guestPlayerCreateResponse = new GuestPlayerCreateResponse(userBuilder.getReguser());
		
		this.setGuestPlayerCreateResponse(guestPlayerCreateResponse);
		
		return SUCCESS;
		
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
	
	public GuestPlayerCreateResponse getGuestPlayerCreateResponse() {
		return guestPlayerCreateResponse;
	}

	public void setGuestPlayerCreateResponse(GuestPlayerCreateResponse guestPlayerCreateResponse) {
		this.guestPlayerCreateResponse = guestPlayerCreateResponse;
	}

	public class GuestPlayerCreateResponse {
		
		private String firstName;
		private String lastName;
		private String userId;
		private String accountRedeemToken;
		private int statusCode = 0;
		
		public GuestPlayerCreateResponse(Reguser reguser) {
			this.setFirstName(reguser.getFirstName());
			this.setLastName(reguser.getLastName());
			this.setUserId(reguser.getUserId());
			this.setAccountRedeemToken(reguser.getAccountRedeemToken().getRedeemToken());
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

		public String getAccountRedeemToken() {
			return accountRedeemToken;
		}

		public void setAccountRedeemToken(String accountRedeemToken) {
			this.accountRedeemToken = accountRedeemToken;
		}

		public int getStatusCode() {
			return statusCode;
		}
		
	}
	
}
