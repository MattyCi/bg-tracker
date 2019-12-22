package org.bgtrack.actions.season;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

import org.bgtrack.utils.BGTConstants;

import org.bgtrack.utils.HibernateUtil;
import org.bgtrack.utils.RoundResultArrayList;
import org.bgtrack.utils.RoundResultArrayListImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;
import org.apache.struts2.interceptor.HttpParametersAware;
import org.bgtrack.auth.ShiroBaseAction;
import org.bgtrack.models.Round;
import org.bgtrack.models.RoundResult;
import org.bgtrack.models.ScoringType;
import org.bgtrack.models.Season;
import org.bgtrack.models.daos.AuthorizationDAO;
import org.bgtrack.models.daos.RoundDAO;
import org.bgtrack.models.daos.SeasonDAO;
import org.bgtrack.models.user.Reguser;
import org.bgtrack.models.user.authorization.Permission;
import org.bgtrack.models.user.authorization.UserPermission;
import org.bgtrack.models.user.daos.UserDAO;

public class RoundCreate extends ShiroBaseAction implements HttpParametersAware {
	private static final long serialVersionUID = -6328260956217475993L;
	
	private static final Logger LOG = LogManager.getLogger(RoundCreate.class);
	
	private String seasonName;
	private String seasonId;
	private String seasonEndDate;
	private boolean errorsOccured = false;
	private HttpParameters parameters;
	Season season;
	Round round;
	RoundResultArrayList roundResultList;
	
	private String createRoundPermissionValue;
	
	private static final String ROUND_CREATE_PERMISSIONS_ERROR_TEXT = "Sorry, only current players of a season can create rounds!";
	private static final String MORE_PLAYERS_REQUIRED_ERROR_TEXT = "More than one player is required to create a round.";
	private static final String TWO_OF_SAME_PLAYER_ERROR_TEXT = "The same player was added to the round twice. Please try again.";
	private static final String PLACES_INVALID_ERROR_TEXT = "The places submitted were not in a valid order.";
	
	@Override
	public Boolean isCsrfProtected() {
		return true;
	}
	
	@Override
	public void validate() {
		
		super.validate();
		
		if (null == seasonId || seasonId.length() == 0) {
			
			LOG.info("parameter seasonId was empty for request to create round by " + shiroUser.getPrincipal());
			
			addActionError(BGTConstants.CHECK_FIELDS);
			
			return;
			
		}
		
		this.season = SeasonDAO.getSeasonById(Integer.parseInt(seasonId), true);
		
		if (season == null) {
			
			LOG.info("season with id" + seasonId + " does not exist for request to create round by " + shiroUser.getPrincipal());
			
			addActionError(BGTConstants.CHECK_FIELDS);
			
			return;
			
		}
		
		if ("I".equals(this.season.getStatus())) {
			
			LOG.info("season with id" + seasonId + " is inactive, but user " + shiroUser.getPrincipal() + " is trying to add a round.");
			
			addActionError(BGTConstants.SEASON_INACTIVE_ERROR);
			
			return;
			
		}
		
		createRoundPermissionValue = "season:createround:"+this.seasonId;
		
		if (!this.isExecutingUserPermitted(createRoundPermissionValue)) {
			
			LOG.info("user " + shiroUser.getPrincipal() + " is trying to add a round to season "+ seasonId + " but they lack permissions.");
			
			addActionError(ROUND_CREATE_PERMISSIONS_ERROR_TEXT);
			
			throw new AuthorizationException("User: " + this.getShiroUser().getPrincipal() + " does not have the required "
					+ "permissions: "+ createRoundPermissionValue + " for action: " + this.getClass() );
			
		}
		
		if(!multiplePlayersPlayed()) {
			
			LOG.info("user " + shiroUser.getPrincipal() + " is trying to add a round to season "+ seasonId + ", but they've added "
					+ "only one player to the round.");
			
			addActionError(MORE_PLAYERS_REQUIRED_ERROR_TEXT);
			
		}
		
	}
	
	private boolean multiplePlayersPlayed() {
		if(!this.getParameters().contains("roundPlayer1"))
			return false;
		
		return true;
	}

	public String execute() {

		this.roundResultList = new RoundResultArrayListImpl();
		
		Round round = new Round();
		Timestamp roundStartTimestamp = new Timestamp(System.currentTimeMillis());
		round.setRoundDate(roundStartTimestamp);
		round.setSeason(this.season);
		round.setCreator(this.regUser);
				
		// 12 max players
		for (int i = 0; i < 11; i++ ) {
			Parameter userId;
			if (this.getParameters().contains("roundPlayer"+i)) {
				userId = this.getParameters().get("roundPlayer"+i);
			} else {
				break;
			}
			
			BigInteger userPlace = new BigInteger(this.getParameters().get("playerPlace"+i).toString());
			
			Reguser roundUser = UserDAO.getUserByID(userId.toString());
			
			if (!this.roundResultList.isEmpty() && isUserAlreadyAdded(roundUser.getUserId())) {
				
				LOG.info("user " + shiroUser.getPrincipal() + " is trying to add a round to season "+ seasonId + ", but they've added "
						+ "two of the same player to the same round.");
				
				addActionError(TWO_OF_SAME_PLAYER_ERROR_TEXT);
				return ERROR;
			}
			
			if (null != roundUser) {
				buildRoundResultList(roundUser, userPlace, round);
			}

		}
		
		if( !arePlacesValid(this.roundResultList) ) {
			
			LOG.info("user " + shiroUser.getPrincipal() + " is trying to add a round to season "+ seasonId + ", but they've added "
					+ "places which don't make sense.");
			
			addActionError(PLACES_INVALID_ERROR_TEXT);
			return ERROR;
		}
		
		buildSeasonPermissionsForNewPlayers();
		
		round.setRoundResults(this.roundResultList);
				
		HibernateUtil.persistObject(round);
		
		recalculateSeasonScoring();
		
		if (errorsOccured) {
			return BGTConstants.ERROR;
		}

		return BGTConstants.SUCCESS;
	}

	private Boolean isUserAlreadyAdded(String userId) {

		for (RoundResult roundResult : roundResultList) {
			
			if (roundResult.getReguser().getUserId().equals(userId))
				return true;
			
		}
		
		return false;
		
	}

	private void buildRoundResultList(Reguser roundUser, BigInteger userPlace, Round round) {
		RoundResult roundResult = new RoundResult();
		roundResult.setRound(round);
		roundResult.setReguser(roundUser);
		roundResult.setPlace(userPlace);
		
		determinePointsEarned(roundResult);
		
		this.roundResultList.add(roundResult);
	}

	private void determinePointsEarned(RoundResult roundResult) {
		int points = 0;
		int place = roundResult.getPlace().intValue();
		
		switch (place) {
		case 1:
			points = 10;
			break;
		case 2:
			points = 9;
			break;
		case 3:
			points = 8;
			break;
		case 4:
			points = 7;
			break;
		case 5:
			points = 6;
			break;
		case 6:
			points = 5;
			break;
		case 7:
			points = 4;
			break;
		case 8:
			points = 3;
			break;
		case 9:
			points = 2;
			break;
		default:
			points = 1;
		}

		roundResult.setPoints(points);
		
		if (isSeasonScoringTypeLayered()) {
			
			LOG.debug("season has layered scoring, adding layered points for player: " + 
					roundResult.getReguser().getFirstName() + roundResult.getReguser().getLastName());
			
			roundResult.setLayeredPoints(determineModifier(roundResult));
			
		}
		
		LOG.debug("player: " + roundResult.getReguser().getFirstName() + roundResult.getReguser().getLastName()
				+ " has earned " + roundResult.getPoints() + roundResult.getLayeredPoints() + " points" );
		
	}

	private boolean isSeasonScoringTypeLayered() {
		
		if(ScoringType.LAYERED.toString().equals(this.season.getScoringType())) {
			return true;
		} else {
			return false;
		}
		
	}

	private double determineModifier(RoundResult roundResult) {
		int occurancesOfPlace = determineOccuarancesOfPlace(roundResult);
		
		/**
		 * Players do not receive extra layered points from the first occurrence of a place in a round.
		 * ie. a player comes in 3rd place for two rounds. For the first occurrence of 3rd place, they
		 * are not rewarded any extra points. But the second time they came in 3rd, they got an extra
		 * 0.25 points.
		 */
		if (occurancesOfPlace >= 1) {
			double bonusPointModifier = 0.25;
			
			return bonusPointModifier;
			
			// uncomment below for additive layering points
			// return (occurancesOfPlace-1) * bonusPointModifier;
		} else {
			return 0;
		}
	}

	// might be more performant to just make a database call for this...
	private int determineOccuarancesOfPlace(RoundResult roundResult) {
		List<RoundResult> usersRoundResults = RoundDAO.getRoundResultsForUserBySeasonId(roundResult.getRound().getSeason().getSeasonId(), 
				roundResult.getReguser().getUserId());
		int occurances = 0;
		
		for (RoundResult result : usersRoundResults) {
			if (roundResult.getPlace().compareTo(result.getPlace()) == 0) {
				occurances++;
			}
		}
		
		LOG.debug("player: " + roundResult.getReguser().getFirstName() + roundResult.getReguser().getLastName()
				+ " has placed in " + roundResult.getPlace() + occurances + " times" );
		
		return occurances;
	}
	
	private boolean arePlacesValid(List<RoundResult> roundResultList) {

		if (roundResultList.size() < 2)
			return false;
		
		BigInteger lastPlaceValue = determineLastPlaceValue();
		
		// from here we need to check the round result list for the existence of each place
		// that is lower than itself but not less than one.
		if( isSequenceFromLastPlaceValid(lastPlaceValue.intValue()) )
			return true;
		
		return false;
	}

	private BigInteger determineLastPlaceValue() {
		
		BigInteger lastPlaceValue = BigInteger.ONE;
		
		for (RoundResult roundResult : roundResultList) {
			
			BigInteger currentPlace = roundResult.getPlace();
			
			if (currentPlace.compareTo(lastPlaceValue) == 1)
				lastPlaceValue = currentPlace;
			
		}
		
		LOG.debug("last place value: " + lastPlaceValue);
		
		return lastPlaceValue;
		
	}
	
	private boolean isSequenceFromLastPlaceValid(int lastPlaceValue) {

		for (int i = lastPlaceValue; i > 0; i--) {
			
			if (!roundResultList.doesRoundResultListContainPlace(i)) {
				
				LOG.info("round result list missing place: " + i + ", but this place should exist");
				
				return false;
			}
			
		}
		
		return true;
	}

	private void buildSeasonPermissionsForNewPlayers() {

		for (RoundResult roundResult : roundResultList) {
			
			if (!playerHasRoundCreatePermission(roundResult.getReguser())) {
				grantPlayerRoundCreatePermission(roundResult.getReguser());
			}
			
		}

	}
	
	private boolean playerHasRoundCreatePermission(Reguser roundUser) {
		
		String principal = roundUser.getEmail();
		String realmName = "jdbcRealm";
		PrincipalCollection principals = new SimplePrincipalCollection(principal, realmName);
		Subject roundPlayerSubject = new Subject.Builder().principals(principals).buildSubject();
		
		if (roundPlayerSubject.isPermitted(createRoundPermissionValue)) {
			
			LOG.debug("player " + roundUser.getFirstName() + roundUser.getLastName() + " already has round create permission");
			
			return true;
		}
		
		LOG.debug("player " + roundUser.getFirstName() + roundUser.getLastName() + " missing round create permission");
		
		return false;
	}
	
	private void grantPlayerRoundCreatePermission(Reguser roundUser) {
		
		LOG.debug("granting player " + roundUser.getFirstName() + roundUser.getLastName() + " round create permission");
		
		Permission permission = AuthorizationDAO.getPermissionByValue(createRoundPermissionValue);
		
		if (permission == null) {
			
			permission = createNewPermission(roundUser);
			
		}
		
		associateUserWithPermission(roundUser, permission);
		
	}

	private Permission createNewPermission(Reguser roundUser) {
		
		Permission permission;
		permission = new Permission();
		permission.setPermValue(createRoundPermissionValue);
		HibernateUtil.persistObject(permission);
		
		return permission;

	}
	
	private void associateUserWithPermission(Reguser roundUser, Permission permission) {
		
		UserPermission userPermission = new UserPermission();
		userPermission.setPermission(permission);
		userPermission.setUser(roundUser);
		HibernateUtil.persistObject(userPermission);
		
	}

	private void recalculateSeasonScoring() {
		
		LOG.debug("recalculating season scoring for scoring type: " + this.season.getScoringType());
		
		SeasonStandingHelper seasonStandingHelper = SeasonStandingHelperFactory.getSeasonStandingHelper(this.season.getScoringType());
		seasonStandingHelper.setSeason(this.season);
		seasonStandingHelper.buildStandings();
	}

	public String getSeasonName() {
		return seasonName;
	}

	public void setSeasonName(String seasonName) {
		this.seasonName = seasonName;
	}

	public String getSeasonId() {
		return seasonId;
	}

	public void setSeasonId(String seasonId) {
		this.seasonId = seasonId;
	}

	public String getSeasonEndDate() {
		return seasonEndDate;
	}

	public void setSeasonEndDate(String seasonEndDate) {
		this.seasonEndDate = seasonEndDate;
	}

	public HttpParameters getParameters() {
		return this.parameters;
	}

	public void setParameters(HttpParameters parameters) {
		this.parameters = parameters;
	}
	
	public Season getSeason() {
		return season;
	}

	public void setSeason(Season season) {
		this.season = season;
	}

	public RoundResultArrayList getRoundResultList() {
		return roundResultList;
	}

	public void setRoundResultList(RoundResultArrayList roundResultList) {
		this.roundResultList = roundResultList;
	}

	public Round getRound() {
		return round;
	}

	public void setRound(Round round) {
		this.round = round;
	}

}
