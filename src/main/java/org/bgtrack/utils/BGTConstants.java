package org.bgtrack.utils;

/**
 * Holds constant variables to be used throughout the price-watcer application.
 * @author Matt
 */
public final class BGTConstants {
	// struts messages
	public static final String SUCCESS = "success";
	public static final String ERROR = "error";

	// error messages
	public static final String GENERIC_ERROR = "Sorry, we could not process your request.";
	public static final String INVALID_USERNAME = "The username entered is invalid!";
	public static final String PASSWORD_MISMATCH = "The passwords provided do not match!";
	public static final String NO_ACCOUNT = "Sorry... we could not log you in. Please check your "
			+ "credentials and try again.";
	public static final String ALREADY_LOGGED_IN = "It seems you are already logged in as a user. "
			+ "Please log out and try to register again.";
	public static String CHECK_FIELDS = "Please check the data submitted and try again.";
	public static final String AUTHENTICATION_ERROR = "User not authenticated, please log in once more.";
	public static final String AUTHORIZATION_ERROR = "User not authorized to run this command.";
	public static final String LOG_IN_PROMPT = "Please log in to perform this action.";
	public static final String DATE_ERROR = "There was an error with the format of the date provided.";
	public static final String SEASON_ID_ERROR = "The season ID was incorrect.";
	public static final String SEASON_INACTIVE_ERROR = "This season has ended, so rounds cannot be created for it.";
	public static final String SCORING_TYPE_EMPTY = "Please provide a season scoring type.";
	
}
