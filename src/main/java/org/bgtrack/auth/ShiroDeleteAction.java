package org.bgtrack.auth;

import org.bgtrack.utils.HibernateUtil;

import PropertiesSelection.PopupMessages;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.bgtrack.auth.UserBuilder.UserBuilderException;

public class ShiroDeleteAction extends ShiroBaseAction {
	private static final long serialVersionUID = -6328260956217475993L;
	
	private static final Logger LOG = LogManager.getLogger(ShiroDeleteAction.class);
	
	private String currentPassword;
	
	private static final String CURRENT_PASSWORD_NOT_PROVIDED_ERROR_TEXT = "You must provide your current password to make any account changes.";
	private static final String CURRENT_PASSWORD_INVALID_ERROR_TEXT = "The current password provided was invalid, please try again.";
	
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
		
		try {
			
			UserBuilder userBuilder = new UserBuilder(regUser);
			
			String currentTimestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			
			String deletedUsername = "DELETED" + currentTimestamp;
			
			userBuilder.buildUsername(deletedUsername);
			
			String newDeletedPassword = userBuilder.generateRandomPassword();
						
			userBuilder.buildPassword(newDeletedPassword, newDeletedPassword);
			
		} catch (UserBuilderException e) {
			LOG.info("Error when user {} tried updating their account. Error is: {}", shiroUser.getPrincipal(), e.getMessage());
			addActionError(e.getMessage());
			return ERROR;
		}
		
		HibernateUtil.updateObject(regUser);
		
		shiroUser.logout();
		
		setPopupMessage(PopupMessages.ACCOUNT_DELETE_SUCCESS);
		
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
					regUser.getUsername(), ae.getMessage());
			
			throw ae;
			
		}
		
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}
	
}
