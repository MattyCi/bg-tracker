package org.bgtrack.auth;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;
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
	
	private static final String EMPTY_USERNAME_ERROR_TEXT = "No username was provided.";
	private static final String INVALID_USERNAME_ERROR_TEXT = "Your username can only contain alphanumeric characters.";
	private static final String USERNAME_TOO_LONG_ERROR_TEXT = "The username provided was too long.";
	private static final String USERNAME_ALREADY_EXISTS_ERROR_TEXT = "The username provided is already in use.";
	
	public static final String EMPTY_PASSWORD_ERROR_TEXT = "One of the passwords provided was empty.";
	public static final String PASSWORD_MISMATCH_ERROR_TEXT = "The passwords provided do not match.";
	
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
			throw new UserBuilderException(EMPTY_USERNAME_ERROR_TEXT);
		
		if (username.length() > 35)
			throw new UserBuilderException(USERNAME_TOO_LONG_ERROR_TEXT);
		
		Pattern specialCharRegex = Pattern.compile("[^A-Za-z0-9]");
		
		Matcher specialCharMatcher = specialCharRegex.matcher(username);
		
		if (specialCharMatcher.find()) {
			throw new UserBuilderException(INVALID_USERNAME_ERROR_TEXT);
		}
		
		if(UserDAO.getUserByUsername(username) != null) {
			throw new UserBuilderException(USERNAME_ALREADY_EXISTS_ERROR_TEXT);
		}
		
		username = username.trim();
				
		this.regUser.setUsername(username);
		
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
		
		Timestamp dateCreated = new Timestamp(System.currentTimeMillis());
		acctRedeemToken.setDateCreated(dateCreated);
		
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
