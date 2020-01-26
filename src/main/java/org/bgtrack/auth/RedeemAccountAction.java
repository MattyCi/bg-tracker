package org.bgtrack.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bgtrack.auth.UserBuilder.UserBuilderException;
import org.bgtrack.models.user.AccountRedeemToken;
import org.bgtrack.models.user.Reguser;
import org.bgtrack.models.user.daos.UserDAO;
import org.bgtrack.utils.HibernateUtil;

public class RedeemAccountAction extends ShiroBaseAction {
	private static final long serialVersionUID = 1L;
	
	protected static final Logger LOG = LogManager.getLogger(RedeemAccountAction.class);
	
	private static final String MISSING_TOKEN_ERROR_TEXT = "The provided token was empty or missing.";
	private static final String TOKEN_NOT_FOUND_ERROR_TEXT = "We were unable to find a user associated with the provided token.";
	
	private static final String SUCCESS_TEXT = "Welcome to SeasonGG! You redeemed your account. "
			+ "Please take note of your username and password, since you will need those to log in again.";
	
	private String submittedToken;
	
	private String username;
	
	private String password;
	private String passwordVerify;
	
	@Override
	public boolean isAuthenticationRequired() {
		return false;
	}
	
	@Override
	public void validate() {
		
		super.validate();
		
		if (submittedToken == null || submittedToken.isEmpty()) {
			LOG.info("token submitted was null or empty");
			addActionError(MISSING_TOKEN_ERROR_TEXT);
		}
		
	}
	
	@Override
	public String execute() throws Exception {
		
		AccountRedeemToken acctRedeemToken = UserDAO.getAccountRedeemToken(submittedToken);
		
		if (acctRedeemToken == null) {
			LOG.info("token {} was not found in the database", submittedToken);
			addActionError(TOKEN_NOT_FOUND_ERROR_TEXT);
			return ERROR;
		}
		
		Reguser redeemedUser = acctRedeemToken.getReguser();
		
		UserBuilder userBuilder = new UserBuilder(redeemedUser);
		
		try {
			
			userBuilder.buildPassword(password, passwordVerify);
			HibernateUtil.updateObject(redeemedUser);
			
		} catch (UserBuilderException e) {
			LOG.info("Error when user {} tried redeeming their account. Error is: {}", redeemedUser.getUsername(), e.getMessage());
			addActionError(e.getMessage());
			return ERROR;
		} catch (Exception e) {
			LOG.error("Unexpected error when user {} tried redeeming their account. Error is: {}", redeemedUser.getUsername(), e.getMessage());
			throw e;
		}
		
		this.setUsername(redeemedUser.getUsername());
		this.setPopupMessage(SUCCESS_TEXT);
		
		HibernateUtil.deleteEntity(acctRedeemToken);
		
		return SUCCESS;
		
	}

	public String getSubmittedToken() {
		return submittedToken;
	}

	public void setSubmittedToken(String submittedToken) {
		this.submittedToken = submittedToken;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordVerify() {
		return passwordVerify;
	}

	public void setPasswordVerify(String passwordVerify) {
		this.passwordVerify = passwordVerify;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String userName) {
		this.username = userName;
	}

}
