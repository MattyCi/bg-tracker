package org.bgtrack.utils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.bgtrack.models.user.Reguser;
import org.bgtrack.models.user.daos.UserDAO;
import org.passay.PasswordValidator;

/**
 * Utility class that contains helper methods for user related functions.
 * @author Matt
 */
public class UserUtils {
	
	/**
	 * Builds a passay PasswordValidator object. This method simply reduces code
	 * clutter by building the object within this method.
	 * @return The passay validator object with configured contructors.
	 */
	public static PasswordValidator createPasswordValidator() {
		PasswordValidator validator = new PasswordValidator(
			/*// length between 8 and 16 characters
			new LengthRule(8, 16),
			
			// at least one upper-case character
			new CharacterRule(EnglishCharacterData.UpperCase, 1),
			
			// at least one lower-case character
			new CharacterRule(EnglishCharacterData.LowerCase, 1),
			
			// at least one digit character
			new CharacterRule(EnglishCharacterData.Digit, 1),
			
			// no whitespace
			new WhitespaceRule()*/);
		
		return validator;
	}
	
	public static void generatePassword(Reguser user, String plainTextPassword) {
		RandomNumberGenerator randomNum = new SecureRandomNumberGenerator();
		Object salt = randomNum.nextBytes();

		String hashedPassword = new Sha256Hash(plainTextPassword, salt, 1024).toBase64();

		user.setPassword(hashedPassword);
		user.setSalt(salt.toString());
	}
	
	public static String getCurrentUserId() {
		Subject currentShiroSubject = SecurityUtils.getSubject();
		
		String username = currentShiroSubject.getPrincipal().toString();
		Reguser currentUser = UserDAO.getUserByUsername(username);
		
		return currentUser.getUserId();
	}
	
}
