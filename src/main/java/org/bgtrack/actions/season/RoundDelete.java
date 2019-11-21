package org.bgtrack.actions.season;

import org.bgtrack.auth.ShiroBaseAction;
import org.bgtrack.models.Round;
import org.bgtrack.models.Season;
import org.bgtrack.models.daos.RoundDAO;
import org.bgtrack.utils.BGTConstants;
import org.bgtrack.utils.HibernateUtil;

public class RoundDelete extends ShiroBaseAction {
	private static final long serialVersionUID = 8752947492063852728L;
	
	private static final String roundDeletePermissionsErrorText = "Sorry, only a season's creator or admin can delete a round!";
	
	private String roundId;
	private String seasonId;
	
	Round roundToDelete;
	Season seasonContainingRound;

	@Override
	public void validate() {
		
		super.validate();
		
		if (roundId == null || roundId.isEmpty()) {
			this.addActionError(BGTConstants.checkFields);
		}
		
		roundToDelete = RoundDAO.getRoundById(roundId);
		
		seasonContainingRound = roundToDelete.getSeason();
		
		this.setSeasonId(seasonContainingRound.getSeasonId().toString());
		
		if (!this.isPermitted("season:deleteround:"+this.seasonId)) {
			this.addActionError(roundDeletePermissionsErrorText);
		}
		
	}
	
	@Override
	public String execute() throws Exception {
	
		HibernateUtil.deleteEntity(roundToDelete);
		
		recalculateSeasonScoring(seasonContainingRound);
		
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
