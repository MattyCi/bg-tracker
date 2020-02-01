package org.bgtrack.utils;

import java.util.ArrayList;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.bgtrack.models.user.Reguser;
import org.bgtrack.models.user.daos.UserDAO;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordValidator;
import org.passay.WhitespaceRule;

/**
 * Utility class that contains helper methods for user related functions.
 * @author Matt
 */
public class UserUtils {
	
	public static final int pwMin = 8;
	public static final int pwMax = 16;
	
	private static final LengthRule lengthRule = new LengthRule(pwMin, pwMax);
	private static final CharacterRule upperCaseRule = new CharacterRule(EnglishCharacterData.UpperCase, 1);
	private static final CharacterRule lowerCaseRule = new CharacterRule(EnglishCharacterData.LowerCase, 1);
	private static final CharacterRule digitRule = new CharacterRule(EnglishCharacterData.Digit, 1);
	private static final WhitespaceRule whitespaceRule = new WhitespaceRule();
	
	private static ArrayList<CharacterRule> pwRules = null;
	
	/**
	 * Builds a passay PasswordValidator object. This method simply reduces code
	 * clutter by building the object within this method.
	 * @return The passay validator object with configured contructors.
	 */
	public static PasswordValidator createPasswordValidator() {
		PasswordValidator validator = new PasswordValidator(lengthRule, upperCaseRule, lowerCaseRule, digitRule, whitespaceRule);
		
		return validator;
	}
	
	public static ArrayList<CharacterRule> getPwCharacterRules() {

		if (pwRules == null) {
			
			pwRules = new ArrayList<CharacterRule>();
			
			pwRules.add(upperCaseRule);
			pwRules.add(lowerCaseRule);
			pwRules.add(digitRule);
			
			return pwRules;
			
		} else {
			
			return pwRules;
			
		}
		
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
