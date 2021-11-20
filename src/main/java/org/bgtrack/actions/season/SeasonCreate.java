package org.bgtrack.actions.season;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import PropertiesSelection.PopupMessages;

import org.bgtrack.utils.BGTConstants;

import org.bgtrack.utils.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bgtrack.auth.ShiroBaseAction;
import org.bgtrack.models.Game;
import org.bgtrack.models.Season;
import org.bgtrack.models.daos.GameDAO;
import org.bgtrack.models.user.authorization.Permission;
import org.bgtrack.models.user.authorization.UserPermission;

public class SeasonCreate extends ShiroBaseAction {
	private static final long serialVersionUID = -6328260956217475993L;
	
	private static final Logger LOG = LogManager.getLogger(SeasonCreate.class);
	
	private String seasonName;
	
	private String seasonGameId;
	private String seasonGameName;
	
	private String seasonEndDate;
	private Timestamp seasonEndTimestamp = null;
	private Timestamp seasonStartTimestamp = new Timestamp(System.currentTimeMillis());
	
	private String seasonScoringType;
	private boolean errorsOccured = false;

	private String createdSeasonId;
	
	private static final String SEASON_NAME_TOO_SHORT_ERROR_TEXT = "Season names must be at least 4 characters long.";
	private static final String SEASON_NAME_TOO_LONG_ERROR_TEXT = "Season names cannot be more than 56 characters long.";
	
	private static final String NO_GAME_SELECTED_ERROR_TEXT = "Please select a game to be played throughout the season.";

	private static final String INVALID_END_DATE_ERROR_TEXT = "The season end date provided was invalid, please "
			+ "choose a valid date with the format mm/dd/yyyy and try again.";
	private static final String END_DATE_BEFORE_START_ERROR_TEXT = "Please choose a date in the future for your season end date.";
	
	private static final String SEASON_NAME_EXISTS_ERROR_TEXT = "Sorry, but the season name you provided already exists.";
	
	@Override
	public Boolean isCsrfProtected() {
		return true;
	}
	
	@Override
	public void validate() {
		
		super.validate();
		
		validateSeasonName();
		
		validateSeasonDates();
		
		validateGameId();
		
	}

	private void validateSeasonName() {
		
		if (seasonName == null || seasonName.isEmpty()) {
			addActionError(SEASON_NAME_TOO_SHORT_ERROR_TEXT);
			return;
		}
		
		seasonName = seasonName.trim();
		
		if (seasonName.length() < 4) {
			addActionError(SEASON_NAME_TOO_SHORT_ERROR_TEXT);
			return;
		} else if (seasonName.length() > 56) {
			addActionError(SEASON_NAME_TOO_LONG_ERROR_TEXT);
			return;
		}
				
	}
	
	private void validateSeasonDates() {
		
		if (seasonEndDate == null || seasonEndDate.isEmpty()) {
			addActionError(INVALID_END_DATE_ERROR_TEXT);
			return;
		}
		
		try {
			
		    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		    Date parsedDate = dateFormat.parse(seasonEndDate);
		    seasonEndTimestamp = new java.sql.Timestamp(parsedDate.getTime());
		    
		    if (seasonEndTimestamp.before(seasonStartTimestamp)) {
		    	
		    	LOG.info("seasonEndTimestamp is before seasonStartTimestamp: " + shiroUser.getPrincipal());
		    	
		    	addActionError(END_DATE_BEFORE_START_ERROR_TEXT);
		    	
		    }
		    
		} catch (Exception e) {
			
			LOG.error("unexpected error validating season end date: " + shiroUser.getPrincipal(), e);
			
			addActionError(INVALID_END_DATE_ERROR_TEXT);
			
		}
		
	}
	
	private void validateGameId() {
		
		if (seasonGameId == null || seasonGameId.isEmpty()) {
			
			LOG.info("no game selected: " + shiroUser.getPrincipal());
			
			addActionError(NO_GAME_SELECTED_ERROR_TEXT);
			return;
		}
		
	}

	public String execute() throws NumberFormatException, Exception {
		
		if (!this.shiroUser.isAuthenticated()) {
			
			LOG.info("user trying to create season but is not authenticated: " + shiroUser.getPrincipal());
			
			addActionError(BGTConstants.AUTHENTICATION_ERROR);
			return BGTConstants.ERROR;
		}
		
		createSeason(seasonName, Integer.parseInt(seasonGameId));

		if (errorsOccured) {
			return BGTConstants.ERROR;
		}

		this.setPopupMessage(PopupMessages.SEASON_CREATE_CONFIRMATION);
		return BGTConstants.SUCCESS;
	}

	public void createSeason(String seasonName, int seasonGameId) throws Exception {

		
				
		Game bggGame = GameDAO.getGameById(seasonGameId);
		
		if (bggGame == null) {
			bggGame = createGame(seasonGameId);
		}
		
		Season season = new Season();
		season.setName(seasonName);
		season.setGame(bggGame);
		season.setStartDate(seasonStartTimestamp);
		season.setEndDate(seasonEndTimestamp);
		
		if (seasonScoringType == null || "".equals(seasonScoringType)) {
			LOG.info("season scoring type was not provided: " + shiroUser.getPrincipal());
			addActionError(BGTConstants.SCORING_TYPE_EMPTY);
			errorsOccured = true;
			return;
		}
		
		season.setScoringType(seasonScoringType);
		
		season.setStatus("A");
		season.setCreator(this.getRegUser());

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			session.save(season);
			tx.commit();
		} catch (ConstraintViolationException e) {
			tx.rollback();
			LOG.info("User {} tried creating a season with name {}, but name already exists. {}", shiroUser.getPrincipal(), season.getName(), e.getMessage());
			addActionError(SEASON_NAME_EXISTS_ERROR_TEXT);
			errorsOccured = true;
			return;
		} catch (Exception e) {
			tx.rollback();
			LOG.error("Unexpected error occured: "+e);
			errorsOccured = true;
			return;
		} finally {
			session.close();
		}
		
		this.setCreatedSeasonId(season.getSeasonId().toString());
		
		assignUserPermissionsForSeason(season);
		
	}

	private Game createGame(int seasonGameId) {
		Game bggGame;
		bggGame = new Game();
		
		bggGame.setGameId(seasonGameId);
		bggGame.setGameName(seasonGameName);
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			session.save(bggGame);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			LOG.error("Unexpected error occured: "+e);
			addActionError(BGTConstants.GENERIC_ERROR);
			errorsOccured = true;
		} finally {
			session.close();
		}
		return bggGame;
	}

	private void assignUserPermissionsForSeason(Season season) {

		Permission permission = createPermission(season);
		
		associateUserToPermission(season, permission);
		
	}

	private Permission createPermission(Season season) {
		Permission permission;
		permission = new Permission();
		permission.setPermValue("season:*:"+season.getSeasonId());
		HibernateUtil.persistObject(permission);
		return permission;
	}
	
	private void associateUserToPermission(Season season, Permission permission) {
		
		LOG.debug("granting user admin rights for new season: " + shiroUser.getPrincipal());
		
		UserPermission userPermission = new UserPermission();
		userPermission.setPermission(permission);
		userPermission.setUser(season.getCreator());
		HibernateUtil.persistObject(userPermission);
	}

	public String getSeasonName() {
		return seasonName;
	}

	public void setSeasonName(String seasonName) {
		this.seasonName = seasonName;
	}

	public String getSeasonGameId() {
		return seasonGameId;
	}

	public void setSeasonGameId(String seasonGameId) {
		this.seasonGameId = seasonGameId;
	}

	public String getSeasonEndDate() {
		return seasonEndDate;
	}

	public void setSeasonEndDate(String seasonEndDate) {
		this.seasonEndDate = seasonEndDate;
	}

	public String getSeasonScoringType() {
		return seasonScoringType;
	}

	public void setSeasonScoringType(String seasonScoringType) {
		this.seasonScoringType = seasonScoringType;
	}

	public String getCreatedSeasonId() {
		return createdSeasonId;
	}

	public void setCreatedSeasonId(String createdSeasonId) {
		this.createdSeasonId = createdSeasonId;
	}

	public String getSeasonGameName() {
		return seasonGameName;
	}

	public void setSeasonGameName(String seasonGameName) {
		this.seasonGameName = seasonGameName;
	}

}
