package org.bgtrack.actions.season;

import org.bgtrack.utils.BGTConstants;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bgtrack.auth.SeasonUtils;
import org.bgtrack.auth.ShiroBaseAction;
import org.bgtrack.models.Round;
import org.bgtrack.models.ScoringType;
import org.bgtrack.models.Season;
import org.bgtrack.models.SeasonStatus;
import org.bgtrack.models.daos.RoundDAO;
import org.bgtrack.models.daos.SeasonDAO;

public class SeasonView extends ShiroBaseAction {
	private static final long serialVersionUID = -6328260956217475993L;
	private static final Logger LOG = LogManager.getLogger(SeasonView.class);
	
	private String seasonId;
	private Season season;
	private String scoringTypeFullText;
	
	int roundPage;
	private List<Round> paginatedRounds;

	private boolean errorsOccured = false;
	private List<String> listofVictors = new ArrayList<String>();
	
	private static final String AVERAGED_FULL_TEXT = "Averaged";
	private static final String HANDICAP_FULL_TEXT = "Handicap";
	
	private static final String PAGE_NUMBER_ERROR = "Sorry, the page number specified was invalid.";
	
	@SuppressWarnings("unused")
	private boolean seasonStatus;
	
	@Override
	public boolean isAuthenticationRequired() {
		return false;
	}
	
	public String execute() {
		
		if (seasonId.isEmpty() || seasonId.length() == 0) {
			addActionError(BGTConstants.SEASON_ID_ERROR);
			return BGTConstants.ERROR;
		}
		
		try {
			this.setSeason(SeasonDAO.getSeasonById(Integer.parseInt(seasonId), true));
		} catch (NumberFormatException e) {
			addActionError(BGTConstants.SEASON_ID_ERROR);
			return BGTConstants.ERROR;
		}
		
		try {
			paginatedRounds = RoundDAO.getPaginatedRoundsList(roundPage, seasonId);
		} catch (NumberFormatException | IOException e) {
			addActionError(PAGE_NUMBER_ERROR);
			return BGTConstants.ERROR;
		}
		
		this.setListofVictors(SeasonUtils.buildVictors(paginatedRounds));
		
		if (SeasonStatus.ACTIVE.toString().equals(season.getStatus()))
			determineSeasonStatus();
		
		determineScoringType(season.getScoringType());
		
		if (errorsOccured) {
			return BGTConstants.ERROR;
		}

		return BGTConstants.SUCCESS;
	}
	
	private void determineSeasonStatus() {
		
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		
		if(currentTime.after(this.getSeason().getEndDate())) {
			
			LOG.info("currentTime is after season end date, marking seasonId {} inactive", seasonId);
			
			SeasonDAO.markSeasonAsInactive(Integer.parseInt(seasonId));
			
		}
		
	}
	
	private void determineScoringType(String scoringType) {

		if (scoringType == null || "".equals(scoringType))
			return;
		
		if (scoringType.equals(ScoringType.AVERAGED.toString()))
			this.setScoringTypeFullText(AVERAGED_FULL_TEXT);
		
		if (scoringType.equals(ScoringType.HANDICAPPED.toString()))
			this.setScoringTypeFullText(HANDICAP_FULL_TEXT);
		
	}

	public String getSeasonId() {
		return seasonId;
	}

	public void setSeasonId(String seasonId) {
		this.seasonId = seasonId;
	}

	public Season getSeason() {
		return season;
	}

	public void setSeason(Season season) {
		this.season = season;
	}

	public List<String> getListofVictors() {
		return listofVictors;
	}

	public void setListofVictors(List<String> listofVictors) {
		this.listofVictors = listofVictors;
	}

	// commenting getter, because struts will set this property to the JSP layer
	// and it will interfere with the name of our SeasonStatus enum type
	/*public boolean getSeasonStatus() {
		return seasonStatus;
	}*/

	public void setSeasonStatus(boolean seasonStatus) {
		this.seasonStatus = seasonStatus;
	}
	
	public String getScoringTypeFullText() {
		return scoringTypeFullText;
	}

	public void setScoringTypeFullText(String scoringTypeFullText) {
		this.scoringTypeFullText = scoringTypeFullText;
	}

	public int getRoundPage() {
		return roundPage;
	}

	public void setRoundPage(int roundPage) {
		this.roundPage = roundPage;
	}

	public List<Round> getPaginatedRounds() {
		return paginatedRounds;
	}

	public void setPaginatedRounds(List<Round> paginatedRounds) {
		this.paginatedRounds = paginatedRounds;
	}
	
}
