package org.bgtrack.actions.season;

import org.bgtrack.auth.ShiroBaseAction;
import org.bgtrack.models.Round;
import org.bgtrack.models.RoundResult;
import org.bgtrack.models.Season;
import org.bgtrack.models.SeasonStanding;
import org.bgtrack.models.daos.AuthorizationDAO;
import org.bgtrack.models.daos.RoundDAO;
import org.bgtrack.models.user.Reguser;
import org.bgtrack.models.user.authorization.Permission;
import org.bgtrack.utils.BGTConstants;
import org.bgtrack.utils.HibernateUtil;

public class RoundDelete extends ShiroBaseAction {
	private static final long serialVersionUID = 8752947492063852728L;
	
	private static final String ROUND_DELETE_PERMISSIONS_ERROR_TEXT = "Sorry, only a season's creator or admin can delete a round!";
	private static final String ROUND_DELETE_CONFIRMATION_TEXT = "Done! Round deleted successfully!";
	
	private String roundId;
	private String seasonId;
	
	private SeasonStandingHelper seasonStandingHelper;
	
	Round roundToDelete;
	Season seasonContainingRound;

	@Override
	public Boolean isCsrfProtected() {
		return true;
	}
	
	@Override
	public void validate() {
		
		super.validate();
		
		if (roundId == null || roundId.isEmpty()) {
			this.addActionError(BGTConstants.CHECK_FIELDS);
		}
		
		roundToDelete = RoundDAO.getRoundById(roundId);
		
		seasonContainingRound = roundToDelete.getSeason();
		
		this.setSeasonId(seasonContainingRound.getSeasonId().toString());
		
		if (!this.isExecutingUserPermitted("season:deleteround:"+this.seasonId)) {
			this.addActionError(ROUND_DELETE_PERMISSIONS_ERROR_TEXT);
		}
		
	}
	
	@Override
	public String execute() throws Exception {
	
		HibernateUtil.deleteEntity(roundToDelete);
		
		recalculateSeasonScoring(seasonContainingRound);
		
		reassignPermissions();
		
		this.setPopupMessage(ROUND_DELETE_CONFIRMATION_TEXT);
		
		return BGTConstants.SUCCESS;
	}
	
	private void recalculateSeasonScoring(Season seasonContainingRound) {
		seasonStandingHelper = SeasonStandingHelperFactory.getSeasonStandingHelper(seasonContainingRound.getScoringType());
		seasonStandingHelper.setSeason(seasonContainingRound);
		seasonStandingHelper.buildStandings();
	}

	private void reassignPermissions() {
		
		for (RoundResult deletedRoundResult : roundToDelete.getRoundResults()) {
			
			if (userNoLongerInSeason(deletedRoundResult.getReguser())) {
				deleteSeasonPermission(deletedRoundResult.getReguser());
			}
			
		}
		
	}
	
	private boolean userNoLongerInSeason(Reguser userFromDeletedRound) {
		
		for (SeasonStanding newSeasonStanding : seasonStandingHelper.getNewSeasonStandings()) {
			
			if (newSeasonStanding.getReguser().getUserId().equals(userFromDeletedRound.getUserId())) {
				return false;
			}
			
		}

		return true;
	}
	
	private void deleteSeasonPermission(Reguser reguser) {
		
		Permission permissionToRevoke = AuthorizationDAO.getPermissionByValue("season:createround:"+roundToDelete.getSeason().getSeasonId());
		AuthorizationDAO.deletePermissionForUser(reguser, permissionToRevoke);
		
	}
	
	public String getRoundId() {
		return roundId;
	}

	public void setRoundId(String roundId) {
		this.roundId = roundId;
	}

	public String getSeasonId() {
		return seasonId;
	}

	public void setSeasonId(String seasonId) {
		this.seasonId = seasonId;
	}
	
}
