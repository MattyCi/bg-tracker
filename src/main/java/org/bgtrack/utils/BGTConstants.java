package org.bgtrack.utils;

/**
 * Holds constant variables to be used throughout the price-watcer application.
 * @author Matt
 */
public final class BGTConstants {
	// struts messages
	public static final String success = "success";
	public static final String error = "error";
	
	// user values
	public static final char newGuestUserType = 'N';
	public static final char guestUserType = 'G';
	public static final char regUserType = 'R';
	
	// cookie values
	public static final String guestIDCookieName = "guestID";
	public static final String guestTokenCookieName = "guestToken";
	
	// error messages
	public static final String genericError = "Sorry, we could not process your request.";
	public static final String invalidEmail = "The email entered is invalid!";
	public static final String passwordMismatch = "The passwords provided do not match!";
	public static final String noAccount = "Sorry... we could not log you in. Please check your "
			+ "credentials and try again.";
	public static final String alreadyLoggedIn = "It seems you are already logged in as a user. "
			+ "Please log out and try to register again.";
	public static String emptyName = "Either the first name or last name was not provided.";
	public static String checkFields = "Please check the data submitted and try again.";
	public static final String authenticationError = "User not authenticated, please log in once more.";
	public static final String authorizationError = "User not authorized to run this command.";
	public static final String dateError = "There was an error with the format of the date provided.";
	public static final String seasonIdError = "The season ID was incorrect.";
	public static final String seasonInactiveError = "This season has ended, so rounds cannot be created for it.";
}
