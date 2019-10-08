package org.bgtrack.actions.season;

import java.io.Serializable;

import org.bgtrack.auth.ShiroBaseAction;
import org.bgtrack.models.Round;
import org.bgtrack.models.Season;
import org.bgtrack.models.daos.RoundDAO;
import org.bgtrack.utils.BGTConstants;
import org.bgtrack.utils.HibernateUtil;
import org.hibernate.Session;

public class RoundDelete extends ShiroBaseAction {
	private static final long serialVersionUID = 8752947492063852728L;
	
	private String roundId;
	private String seasonId;

	@Override
	public void validate() {
		
		if (roundId == null || roundId.isEmpty()) {
			this.addActionError(BGTConstants.genericError);
			this.isValidationFailed = true;
		}
		
	}
	
	@Override
	public String execute() throws Exception {
		super.execute();

		if (!this.shiroUser.isAuthenticated()) {
			addActionError(BGTConstants.authenticationError);
			return BGTConstants.error;
		}
		
		if (!this.shiroUser.getPrincipal().toString().equals("matt@test.com")) {
			addActionError(BGTConstants.authorizationError);
			return BGTConstants.error;
		}
		
		Round roundToDelete = RoundDAO.getRoundById(roundId);
		
		Season seasonContainingRound = roundToDelete.getSeason();
				
		HibernateUtil.deleteEntity(roundToDelete);
		
		recalculateSeasonScoring(seasonContainingRound);
		
		this.setSeasonId(seasonContainingRound.getSeasonId().toString());
		
		return BGTConstants.success;
	}
	
	private void recalculateSeasonScoring(Season seasonContainingRound) {
		SeasonStandingHelper seasonStandingHelper = SeasonStandingHelperFactory.getSeasonStandingHelper(seasonContainingRound.getScoringType());
		seasonStandingHelper.setSeason(seasonContainingRound);
		seasonStandingHelper.buildStandings();
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
