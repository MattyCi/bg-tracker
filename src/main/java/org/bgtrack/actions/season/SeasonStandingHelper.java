package org.bgtrack.actions.season;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bgtrack.models.RoundResult;
import org.bgtrack.models.Season;
import org.bgtrack.models.SeasonStanding;
import org.bgtrack.models.daos.RoundDAO;
import org.bgtrack.models.daos.SeasonDAO;
import org.bgtrack.models.user.Reguser;
import org.bgtrack.utils.HibernateUtil;

//TODO: Make a delta build standings so that we don't recalculate everything over and over again
public abstract class SeasonStandingHelper {
	private Season season;
	private List<SeasonStanding> newSeasonStandings;
	private int loopIndex = 0;
	
	/**
	 * Rebuilds the season standing model objects for a given season.
	 * Call <code>setSeason(Season season)</code> before calling this method.
	 */
	protected void buildStandings() {
		
		newSeasonStandings = new ArrayList<SeasonStanding>();
		
		for (Reguser user : SeasonDAO.getAllUsersInSeason(season.getSeasonId())) {
			
			buildStandingsForUser(user);
			
		}
		
		calculateSeasonPlaces();
		
		for (SeasonStanding oldSeasonStanding : this.season.getSeasonStandings()) {
			HibernateUtil.deleteEntity(oldSeasonStanding);
		}

		for (SeasonStanding seasonStanding : newSeasonStandings) {
			HibernateUtil.persistObject(seasonStanding);
		}
		
	}

	private void buildStandingsForUser(Reguser user) {

		SeasonStanding seasonStanding = instantiateSeasonStanding(user);
		
		for (RoundResult roundResult : RoundDAO.getRoundResultsForUserBySeasonId(season.getSeasonId(), user.getUserId())) {
			
			calculateSeasonTotalsForUser(roundResult, seasonStanding);
			
		}
		
		calculateAveragePoints(seasonStanding);
		
		newSeasonStandings.add(seasonStanding);
		
	}

	private SeasonStanding instantiateSeasonStanding(Reguser user) {
		SeasonStanding newSeasonStanding = new SeasonStanding();
		newSeasonStanding.setReguser(user);
		newSeasonStanding.setSeason(this.season);
		newSeasonStanding.setPlace(0);
		newSeasonStanding.setTotalPoints(0);
		return newSeasonStanding;
	}
	
	private void calculateAveragePoints(SeasonStanding seasonStanding) {
		double totalAveragePoints = seasonStanding.getTotalPoints() / seasonStanding.getGamesPlayed();
		seasonStanding.setAveragedPoints(roundAveragePoints(totalAveragePoints));
	}

	private double roundAveragePoints(double totalAveragePoints) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.DOWN);
		
		return Double.parseDouble(df.format(totalAveragePoints));
	}

	private void calculateSeasonPlaces() {
		
		Collections.sort(this.newSeasonStandings); // needed to sort by player score descending
		
		for (int i = 0; i < this.newSeasonStandings.size(); i++) {
			determineSeasonStandingPlace();
			loopIndex++;
		}

	}

	private void determineSeasonStandingPlace() {

		if (!isEligibleToCompete()) {
			this.newSeasonStandings.get(loopIndex).setPlace(999);
			return;
		}
		
		if (isTiedWithPreviousPlayer()) {
			this.newSeasonStandings.get(loopIndex).setPlace(loopIndex-1);
		} else {
			this.newSeasonStandings.get(loopIndex).setPlace(loopIndex+1);
		}
		
	}

	private boolean isEligibleToCompete() {
		
		if (this.newSeasonStandings.get(loopIndex).getGamesPlayed() < 8) {
			return false;
		} else {
			return true;
		}
		
	}

	private boolean isTiedWithPreviousPlayer() {
		if (loopIndex != 0) {
			return arePointsEqual();
		} else {
			return false;
		}
	}

	private boolean arePointsEqual() {
		
		if (this.newSeasonStandings.get(loopIndex).getAveragedPoints() == this.newSeasonStandings.get(loopIndex-1).getAveragedPoints()) {
			return true;
		} else {
			return false;
		}
		
	}

	protected abstract void calculateSeasonTotalsForUser(RoundResult roundResult, SeasonStanding seasonStanding);

	public Season getSeason() {
		return season;
	}

	public void setSeason(Season season) {
		this.season = season;
	}

	public List<SeasonStanding> getSeasonStandings() {
		return newSeasonStandings;
	}

	public void setSeasonStandings(List<SeasonStanding> seasonStandings) {
		this.newSeasonStandings = seasonStandings;
	}

}
