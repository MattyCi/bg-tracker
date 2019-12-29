package org.bgtrack.auth;

import org.bgtrack.utils.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.bgtrack.auth.UserBuilder.UserBuilderException;

public class ShiroUpdateAction extends ShiroBaseAction {
	private static final long serialVersionUID = -6328260956217475993L;
	
	private static final Logger LOG = LogManager.getLogger(ShiroUpdateAction.class);
		
	private String username;
	private String password;
	private String passwordVerify;
	private String firstName;
	private String lastName;
	private String currentPassword;

	private boolean passwordChanged = false;
	
	private static final String NO_INPUT_ERROR_TEXT = "Please provide a value to update.";
	private static final String CURRENT_PASSWORD_NOT_PROVIDED_ERROR_TEXT = "You must provide your current password to make any account changes.";
	private static final String CURRENT_PASSWORD_INVALID_ERROR_TEXT = "The current password provided was invalid, please try again.";
	
	private static final String ACCOUNT_UPDATE_SUCCESS_TEXT = "Account updated successfully!";
	
	@Override
	public Boolean isCsrfProtected() {
		return true;
	}
	
	@Override
	public void validate() {
		super.validate();
		
		if (currentPassword == null || currentPassword.isEmpty())
			addActionError(CURRENT_PASSWORD_NOT_PROVIDED_ERROR_TEXT);
		
	}
	
	public String execute() {
		
		try {
			verifyUsersCurrentPassword();
		} catch (AuthenticationException ae) {
			addActionError(CURRENT_PASSWORD_INVALID_ERROR_TEXT);
			return ERROR;
		}
		
		Boolean changesMade = false;
		
		try {
			
			UserBuilder userBuilder = new UserBuilder(regUser);
			
			if (username != null && !username.trim().isEmpty()) {
				userBuilder.buildUsername(username);
				changesMade = true;
			}
			
			if (password != null && !password.trim().isEmpty()) {
				userBuilder.buildPassword(password, passwordVerify);
				changesMade = true;
				passwordChanged  = true;
			}
			
			if (firstName != null && !firstName.trim().isEmpty()) {
				userBuilder.buildFirstName(firstName);
				changesMade = true;
			}
			
			if (lastName != null && !lastName.trim().isEmpty()) {
				userBuilder.buildLastName(lastName);
				changesMade = true;
			}
				
		} catch (UserBuilderException e) {
			LOG.info("Error when user {} tried updating their account. Error is: {}", shiroUser.getPrincipal(), e.getMessage());
			addActionError(e.getMessage());
			return ERROR;
		}
		
		if (!changesMade) {
			LOG.info("Error when user {} tried updating their account. User provided no values to update.", regUser.getEmail());
			addActionError(NO_INPUT_ERROR_TEXT);
			return ERROR;
		}
		
		HibernateUtil.updateObject(regUser);
		
		resetShiroInfo();
		
		setPopupMessage(ACCOUNT_UPDATE_SUCCESS_TEXT);
		
		return SUCCESS;
	}

	private void verifyUsersCurrentPassword() {

		try {
			
			String currentPrincipal = shiroUser.getPrincipal().toString();
			
			UsernamePasswordToken shiroLoginToken;

			shiroLoginToken = new UsernamePasswordToken(currentPrincipal, this.currentPassword);
			
			shiroLoginToken.setRememberMe(true);
			
			shiroUser.login(shiroLoginToken);
			
		} catch (AuthenticationException ae) {
			
			LOG.info("User {} is trying to update their account but password verification failed with the following error: {}",
					regUser.getEmail(), ae.getMessage());
			
			throw ae;
			
		}
		
	}

	private void resetShiroInfo() {
		
		UsernamePasswordToken shiroLoginToken;
		
		if (passwordChanged) {
			shiroLoginToken = new UsernamePasswordToken(regUser.getEmail(), this.password);
		} else {
			shiroLoginToken = new UsernamePasswordToken(regUser.getEmail(), this.currentPassword);
		}
		
		shiroLoginToken.setRememberMe(true);
		
		shiroUser.login(shiroLoginToken);
		
		this.generateCSRFToken();

	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
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
	
	public String getPasswordVerify() {
		return passwordVerify;
	}

	public void setPasswordVerify(String passwordVerify) {
		this.passwordVerify = passwordVerify;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}
	
}
