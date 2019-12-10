package org.bgtrack.actions.season;

import org.bgtrack.auth.ShiroBaseAction;
import org.bgtrack.models.Season;
import org.bgtrack.models.daos.SeasonDAO;
import org.bgtrack.models.user.Reguser;
import org.bgtrack.models.user.daos.UserDAO;
import org.bgtrack.utils.BGTConstants;

public class CalculateUserSeasonStatsAction extends ShiroBaseAction {
	private static final long serialVersionUID = 5894108900392279076L;
	Reguser selectedUser;
	private String selectedUserId;
	
	private Season selectedSeason;
	private int selectedSeasonId;
	
	UserSeasonStats userSeasonStats;
	
	private static final String PLAYER_HAS_NO_ROUNDS_PLAYED_ERROR_TEXT = "The selected player hasn't played any rounds in this season yet.";

	@Override
	public void validate() {
		
		if (this.selectedUserId != null && !this.selectedUserId.isEmpty()) {
			this.selectedUser = UserDAO.getUserByID(selectedUserId);
		} else {
			addActionError(BGTConstants.GENERIC_ERROR);
		}
		
		this.selectedSeason = SeasonDAO.getSeasonById(selectedSeasonId, true);
		
		if (this.selectedSeason == null) {
			addActionError(BGTConstants.GENERIC_ERROR);
		}
		
	}
	
	@Override
	public String execute() throws Exception {
		super.execute();
		
		userSeasonStats = new UserSeasonStats(this.selectedSeason, this.selectedUser);
		
		if (userSeasonStats.getSeasonStandingList().isEmpty()) {
			addActionError(PLAYER_HAS_NO_ROUNDS_PLAYED_ERROR_TEXT);
			return BGTConstants.ERROR;
		}
		
		this.setUserSeasonStats(userSeasonStats);
		
		return BGTConstants.SUCCESS;
	}
	
	public UserSeasonStats getUserSeasonStats() {
		return userSeasonStats;
	}

	public void setUserSeasonStats(UserSeasonStats userSeasonStats) {
		this.userSeasonStats = userSeasonStats;
	}

	public int getSelectedSeasonId() {
		return selectedSeasonId;
	}

	public void setSelectedSeasonId(int selectedSeasonId) {
		this.selectedSeasonId = selectedSeasonId;
	}
	
	public String getSelectedUserId() {
		return selectedUserId;
	}

	public void setSelectedUserId(String selectedUserId) {
		this.selectedUserId = selectedUserId;
	}
	
}
