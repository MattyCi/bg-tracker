package org.bgtrack.actions.season;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.bgtrack.utils.BGTConstants;

import org.bgtrack.utils.HibernateUtil;
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
	private String seasonName;
	private String seasonId;
	private String seasonEndDate;
	private boolean errorsOccured = false;
	private HttpParameters parameters;
	Season season;
	Round round;
	List<RoundResult> roundResultList;
	
	private String createRoundPermissionValue;

	private static final String roundCreatePermissionsErrorText = "Sorry, only current players of a season can create rounds!";
	private static final String MORE_PLAYERS_REQUIRED_ERROR_TEXT = "More than one player is required to create a round.";
	
	@Override
	public void validate() {
		
		super.validate();
		
		if (null == seasonId || seasonId.length() == 0) {
			
			addActionError(BGTConstants.checkFields);
			
			return;
			
		}
		
		this.season = SeasonDAO.getSeasonById(Integer.parseInt(seasonId), true);
		
		if (season == null) {
			
			addActionError(BGTConstants.checkFields);
			
			return;
			
		}
		
		if ("I".equals(this.season.getStatus())) {
			
			addActionError(BGTConstants.seasonInactiveError);
			
			return;
			
		}
		
		createRoundPermissionValue = "season:createround:"+this.seasonId;
		
		if (!this.isExecutingUserPermitted(createRoundPermissionValue)) {
			
			addActionError(roundCreatePermissionsErrorText);
			
			throw new AuthorizationException("User: " + this.getShiroUser().getPrincipal() + " does not have the required "
					+ "permissions: "+ createRoundPermissionValue + " for action: " + this.getClass() );
			
		}
		
		if(!multiplePlayersPlayed()) {
			addActionError(MORE_PLAYERS_REQUIRED_ERROR_TEXT);
		}
		
	}
	
	private boolean multiplePlayersPlayed() {
		if(!this.getParameters().contains("roundPlayer1"))
			return false;
		
		return true;
	}

	public String execute() {

		this.roundResultList = new ArrayList<RoundResult>(); 
		
		Round round = new Round();
		Timestamp roundStartTimestamp = new Timestamp(System.currentTimeMillis());
		round.setRoundDate(roundStartTimestamp);
		round.setSeason(this.season);
		round.setCreator(this.regUser);
		
		// 12 max players
		for (int i = 0; i < 13; i++ ) {
			Parameter userId;
			if (this.getParameters().contains("roundPlayer"+i)) {
				userId = this.getParameters().get("roundPlayer"+i);
			} else {
				break;
			}
			
			BigInteger userPlace = new BigInteger(this.getParameters().get("playerPlace"+i).toString());
			
			Reguser roundUser = UserDAO.getUserByID(userId.toString());
			
			if (null != roundUser) {
				buildRoundResultList(roundUser, userPlace, round);
			}
			
			if (!playerHasRoundCreatePermission(roundUser)) {
				grantPlayerRoundCreatePermission(roundUser);
			}

		}
		
		round.setRoundResults(this.roundResultList);
				
		HibernateUtil.persistObject(round);
		
		recalculateSeasonScoring();
		
		if (errorsOccured) {
			return BGTConstants.error;
		}

		return BGTConstants.success;
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
			roundResult.setLayeredPoints(determineModifier(roundResult));
		}

	}

	private boolean isSeasonScoringTypeLayered() {
		
		if("L".equals(this.season.getScoringType())) {
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
		
		return occurances;
	}
	
	private boolean playerHasRoundCreatePermission(Reguser roundUser) {
		
		String principal = roundUser.getEmail();
		String realmName = "jdbcRealm";
		PrincipalCollection principals = new SimplePrincipalCollection(principal, realmName);
		Subject roundPlayerSubject = new Subject.Builder().principals(principals).buildSubject();
		
		if (roundPlayerSubject.isPermitted(createRoundPermissionValue)) {
			return true;
		}
		
		return false;
	}
	
	private void grantPlayerRoundCreatePermission(Reguser roundUser) {

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

	public List<RoundResult> getRoundResultList() {
		return roundResultList;
	}

	public void setRoundResultList(List<RoundResult> roundResultList) {
		this.roundResultList = roundResultList;
	}

	public Round getRound() {
		return round;
	}

	public void setRound(Round round) {
		this.round = round;
	}

}
