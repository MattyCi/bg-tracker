package org.bgtrack.auth;

import java.sql.Timestamp;

import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.bgtrack.utils.BGTConstants;
import org.bgtrack.utils.UserUtils;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;

import org.bgtrack.utils.HibernateUtil;
import org.bgtrack.models.user.Reguser;
import org.bgtrack.models.user.daos.UserDAO;

public class ShiroRegisterAction extends ShiroBaseAction {
	private static final long serialVersionUID = -6328260956217475993L;
	private String username;
	private String password;
	private String passwordVerify;
	private String firstName;
	private String lastName;
	
	private boolean errorsOccured = false;
	
	public String execute() {
		
		// ensure the user is not already logged in
		if (this.shiroUser.isAuthenticated()) {
			addActionError(BGTConstants.alreadyLoggedIn);
			return BGTConstants.error;
		}
		
		// validate email is valid
		EmailValidator emailValidator = EmailValidator.getInstance();
		
		// isValid will also protect against null strings (no null checks needed)
		if (!emailValidator.isValid(username)) {
			addActionError(BGTConstants.invalidEmail);
			return BGTConstants.error;
		}
		
		if (!password.equals(passwordVerify)) {
			addActionError(BGTConstants.passwordMismatch);
			return BGTConstants.error;
		}
		
		// validate password strength
		PasswordValidator validator = UserUtils.createPasswordValidator();
		RuleResult result = validator.validate(new PasswordData(password));
		
		if (!result.isValid()) {
			addActionError(validator.getMessages(result).get(0));
			return BGTConstants.error;
		}

		// ensure user does not already exist
		if(UserDAO.getUserByEmail(username) != null) {
			addActionError(BGTConstants.genericError);
			return BGTConstants.error;
		}
		
		if (firstName.isEmpty() || lastName.isEmpty() || firstName.length() == 0 || lastName.length() == 0) {
			addActionError(BGTConstants.emptyName);
			return BGTConstants.error;
		}
		
		username = username.toLowerCase();
		
		registerUser(username, password, firstName, lastName);
				
		if (errorsOccured) {
			addActionError(BGTConstants.genericError);
			return BGTConstants.error;
		}

		// if we reach this, user will now finally log in through struts2 action chaining
		return BGTConstants.success;
	}

	public void registerUser(String email, String plainTextPassword, String firstName, String lastName) {
		Timestamp registrationTime = new Timestamp(System.currentTimeMillis());
		
		this.regUser = new Reguser();
		this.regUser.setEmail(email);
		
		UserUtils.generatePassword(this.regUser, plainTextPassword);
		
		this.regUser.setFirstName(firstName);
		this.regUser.setLastName(lastName);
		this.regUser.setRegistrationTime(registrationTime);
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			session.save(this.regUser);
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			System.err.println("Hibernate error occured while registering user: "+e);
			errorsOccured = true;
			throw e;
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
	
}
