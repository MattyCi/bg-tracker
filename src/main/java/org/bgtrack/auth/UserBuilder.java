package org.bgtrack.auth;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.bgtrack.models.user.AccountRedeemToken;
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

	private static final String TOKEN_CREATE_ERROR = "Unable to create account redemption token.";
	
	public UserBuilder(Reguser regUser) throws UserBuilderException {
		
		if (regUser == null)
			throw new UserBuilderException(NULL_REGUSER_ERROR_TEXT);
		
		this.regUser = regUser;
		
		buildRegistrationTime();
		
	}
	
	private void buildRegistrationTime() {
		
		Timestamp registrationTime = new Timestamp(System.currentTimeMillis());
		
		regUser.setRegistrationTime(registrationTime);
		
	}

	public void buildUsername(String username) throws UserBuilderException {
		
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
	
	public void buildPassword(String password, String passwordVerify) throws UserBuilderException {
		
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
		
	public void buildFirstName(String firstName) throws UserBuilderException {
		
		if (firstName == null || firstName.trim().isEmpty())
			throw new UserBuilderException(EMPTY_FIRST_NAME_ERROR_TEXT);
		
		if (firstName.matches(".*\\d.*"))
			throw new UserBuilderException(INVALID_FIRST_NAME_ERROR_TEXT);
		
		this.regUser.setFirstName(firstName);
		
	}
	
	public void buildLastName(String lastName) throws UserBuilderException {
		
		if (lastName == null || lastName.trim().isEmpty())
			throw new UserBuilderException(EMPTY_LAST_NAME_ERROR_TEXT);
		
		if (lastName.matches(".*\\d.*"))
			throw new UserBuilderException(INVALID_LAST_NAME_ERROR_TEXT);
		
		this.regUser.setLastName(lastName);
		
	}
	
	public String generateRandomPassword() {
		
		String newDeletedPassword = RandomStringUtils.randomAlphabetic(12);
		
		Random rand = new Random();
		newDeletedPassword += rand.nextInt();
		newDeletedPassword += rand.nextInt();
		
		return newDeletedPassword;
		
	}
	
	public void createGuestAccountRedeemToken() throws UserBuilderException {
		
		SecureRandom sr = null;
		byte[] random = new byte[32];
		String digest = null;
		
		try {
			sr = SecureRandom.getInstance("SHA1PRNG");
			
			sr.nextBytes(random);
			
			digest = Base64.getEncoder().encodeToString(random);
			
		} catch (Exception e) {
			
			throw new UserBuilderException(TOKEN_CREATE_ERROR);
			
		}

		AccountRedeemToken acctRedeemToken = new AccountRedeemToken();
		acctRedeemToken.setRedeemToken(digest);
		
		this.regUser.setAccountRedeemToken(acctRedeemToken);
		
	}
	
	public void setCreatorForGuestAccountRedeemToken(Reguser creator) throws UserBuilderException {
		
		this.regUser.getAccountRedeemToken().setCreator(creator);
		
	}
	
	public class UserBuilderException extends Exception {
		private static final long serialVersionUID = 4612687637887735068L;

		public UserBuilderException(String message) {
			super(message);
		}
		
	}
	
	public Reguser getReguser() {
		return this.regUser;
	}

}
