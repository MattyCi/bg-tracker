package org.bgtrack.auth;

import org.apache.commons.validator.routines.EmailValidator;
import org.bgtrack.models.user.Reguser;
import org.bgtrack.models.user.daos.UserDAO;
import org.bgtrack.utils.UserUtils;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;

public class UserBuilder {

	private Reguser regUser;
	
	private static final String NULL_REGUSER_ERROR_TEXT = "The regUser model object provided was null.";
	
	private static final String EMPTY_EMAIL_ERROR_TEXT = "No email was provided.";
	private static final String INVALID_EMAIL_ERROR_TEXT = "The email entered is invalid.";
	private static final String EMAIL_ALREADY_EXISTS_ERROR_TEXT = "The email provided is already in use.";
	
	public static final String EMPTY_PASSWORD_ERROR_TEXT = "One of the passwords provided was empty.";
	public static final String PASSWORD_MISMATCH_ERROR_TEXT = "The passwords provided do not match.";
	
	private static final String EMPTY_FIRST_NAME_ERROR_TEXT = "No first name was provided.";
	private static final String INVALID_FIRST_NAME_ERROR_TEXT = "The first name provided was invalid.";
	
	private static final String EMPTY_LAST_NAME_ERROR_TEXT = "No last name was provided.";
	private static final String INVALID_LAST_NAME_ERROR_TEXT = "The last name provided was invalid.";
	
	UserBuilder(Reguser regUser) throws UserBuilderException {
		
		if (regUser == null)
			throw new UserBuilderException(NULL_REGUSER_ERROR_TEXT);
		
		this.regUser = regUser;
		
	}
	
	void buildUsername(String username) throws UserBuilderException {
		
		if (username == null || username.trim().isEmpty())
			throw new UserBuilderException(EMPTY_EMAIL_ERROR_TEXT);
		
		EmailValidator emailValidator = EmailValidator.getInstance();
		
		if (!emailValidator.isValid(username)) {
			throw new UserBuilderException(INVALID_EMAIL_ERROR_TEXT);
		}
		
		if(UserDAO.getUserByEmail(username) != null) {
			throw new UserBuilderException(EMAIL_ALREADY_EXISTS_ERROR_TEXT);
		}
		
		username = username.trim();
		
		username = username.toLowerCase();
		
		this.regUser.setEmail(username);
		
	}
	
	void buildPassword(String password, String passwordVerify) throws UserBuilderException {
		
		if (password == null || passwordVerify == null || password.trim().isEmpty() || passwordVerify.trim().isEmpty())
			throw new UserBuilderException(EMPTY_PASSWORD_ERROR_TEXT);
		
		if (!password.equals(passwordVerify))
			throw new UserBuilderException(PASSWORD_MISMATCH_ERROR_TEXT);
		
		PasswordValidator passwordValidator = UserUtils.createPasswordValidator();
		RuleResult passwordValidatorResult = passwordValidator.validate(new PasswordData(password));
		
		if (!passwordValidatorResult.isValid()) {
			throw new UserBuilderException(passwordValidator.getMessages(passwordValidatorResult).get(0));
		}
		
		UserUtils.generatePassword(this.regUser, password);
		
	}
		
	void buildFirstName(String firstName) throws UserBuilderException {
		
		if (firstName == null || firstName.trim().isEmpty())
			throw new UserBuilderException(EMPTY_FIRST_NAME_ERROR_TEXT);
		
		if (firstName.matches(".*\\d.*"))
			throw new UserBuilderException(INVALID_FIRST_NAME_ERROR_TEXT);
		
		this.regUser.setFirstName(firstName);
		
	}
	
	void buildLastName(String lastName) throws UserBuilderException {
		
		if (lastName == null || lastName.trim().isEmpty())
			throw new UserBuilderException(EMPTY_LAST_NAME_ERROR_TEXT);
		
		if (lastName.matches(".*\\d.*"))
			throw new UserBuilderException(INVALID_LAST_NAME_ERROR_TEXT);
		
		this.regUser.setLastName(lastName);
		
	}
	
	protected class UserBuilderException extends Exception {
		private static final long serialVersionUID = 4612687637887735068L;

		public UserBuilderException(String message) {
			super(message);
		}
		
	}

}
