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

	@Override
	public void validate() {
		
		if (this.selectedUserId != null && !this.selectedUserId.isEmpty()) {
			this.selectedUser = UserDAO.getUserByID(selectedUserId);
		} else {
			addActionError(BGTConstants.genericError);
			this.setValidationFailed(true);
		}
		
		this.selectedSeason = SeasonDAO.getSeasonById(selectedSeasonId, true);
		
		if (this.selectedSeason == null) {
			addActionError(BGTConstants.genericError);
			this.setValidationFailed(true);
		}
		
	}
	
	@Override
	public String execute() throws Exception {
		super.execute();
		
		userSeasonStats = new UserSeasonStats(this.selectedSeason, this.selectedUser);
		
		this.setUserSeasonStats(userSeasonStats);
		
		return BGTConstants.success;
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