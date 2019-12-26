package org.bgtrack.actions.season;

import org.bgtrack.models.RoundResult;
import org.bgtrack.models.SeasonStanding;

/**
 * Handicapped season standings are calculated by adding extra points for
 * players who play consistently at lower places.
 * @author Matt
 *
 */
public class HandicappedSeasonStandingHelper extends SeasonStandingHelper {

	@Override
	protected void calculateSeasonTotalsForUser(RoundResult roundResult, SeasonStanding seasonStanding) {
		double totalPoints = seasonStanding.getTotalPoints();
		int gamesPlayed = seasonStanding.getGamesPlayed();
		
		totalPoints += roundResult.getPoints();
		totalPoints += roundResult.getHandicapPoints();
		gamesPlayed++;
		
		seasonStanding.setTotalPoints(totalPoints);
		seasonStanding.setGamesPlayed(gamesPlayed);
	}


}
