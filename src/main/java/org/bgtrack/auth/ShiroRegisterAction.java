package org.bgtrack.auth;

import java.sql.Timestamp;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.bgtrack.utils.BGTConstants;

import org.bgtrack.utils.HibernateUtil;
import org.bgtrack.auth.UserBuilder.UserBuilderException;
import org.bgtrack.models.user.Reguser;

public class ShiroRegisterAction extends ShiroBaseAction {
	private static final long serialVersionUID = -6328260956217475993L;
	private String username;
	private String password;
	private String passwordVerify;
	
	private boolean errorsOccured = false;
	
	@Override
	public boolean isAuthenticationRequired() {
		return false;
	}
	
	public String execute() {
		
		// ensure the user is not already logged in
		if (this.shiroUser.isAuthenticated()) {
			addActionError(BGTConstants.ALREADY_LOGGED_IN);
			return BGTConstants.ERROR;
		}
		
		this.regUser = new Reguser();
		
		try {
			
			UserBuilder userBuilder = new UserBuilder(this.regUser);
			
			userBuilder.buildUsername(username);
			
			userBuilder.buildPassword(password, passwordVerify);
			
		} catch (UserBuilderException e) {
			
			LOG.info("Error when user tried creating account with username: {}. Error is: {}", username, e.getMessage());
			addActionError(e.getMessage());
			return ERROR;
			
		} catch (Exception e) {
			
			LOG.error("Unexpected error when user tried creating account with username: {}. "
					+ "Error is: {}", username, e.getMessage());
			addActionError(GENERIC_ERROR);
			return ERROR;
			
		}

		registerUser();
				
		if (errorsOccured) {
			addActionError(BGTConstants.GENERIC_ERROR);
			return BGTConstants.ERROR;
		}

		// if we reach this, user will now finally log in through struts2 action chaining
		return BGTConstants.SUCCESS;
	}

	public void registerUser() {
		Timestamp registrationTime = new Timestamp(System.currentTimeMillis());
				
		this.regUser.setRegistrationTime(registrationTime);
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			session.save(this.regUser);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			LOG.error("Exception occured while registering user: "+e);
			errorsOccured = true;
		} finally {
			session.close();
		}
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
	
	public String getPasswordVerify() {
		return passwordVerify;
	}

	public void setPasswordVerify(String passwordVerify) {
		this.passwordVerify = passwordVerify;
	}
	
}
