package org.bgtrack.actions.season;

import org.bgtrack.models.RoundResult;
import org.bgtrack.models.SeasonStanding;

/**
 * Averaged scoring seasons calculate the total points
 * divided by the total number of games for each player.
 * @author Matt
 *
 */
public class AveragedSeasonStandingHelper extends SeasonStandingHelper {

	@Override
	protected void calculateSeasonTotalsForUser(RoundResult roundResult, SeasonStanding seasonStanding) {
		double totalPoints = seasonStanding.getTotalPoints();
		int gamesPlayed = seasonStanding.getGamesPlayed();
		
		totalPoints += roundResult.getPoints();
		gamesPlayed++;
		
		seasonStanding.setTotalPoints(totalPoints);
		seasonStanding.setGamesPlayed(gamesPlayed);
	}

}
