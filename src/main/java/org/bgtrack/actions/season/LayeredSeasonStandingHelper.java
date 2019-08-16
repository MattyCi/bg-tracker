package org.bgtrack.actions.season;

import org.bgtrack.models.RoundResult;
import org.bgtrack.models.SeasonStanding;

/**
 * Layered season standings are calculated by adding extra points for
 * players who play consistently at a certain place.
 * @author Matt
 *
 */
public class LayeredSeasonStandingHelper extends SeasonStandingHelper {

	@Override
	protected void calculateSeasonTotalsForUser(RoundResult roundResult, SeasonStanding seasonStanding) {
		double totalPoints = seasonStanding.getTotalPoints();
		int gamesPlayed = seasonStanding.getGamesPlayed();
		
		totalPoints += roundResult.getPoints();
		totalPoints += roundResult.getLayeredPoints();
		gamesPlayed++;
		
		seasonStanding.setTotalPoints(totalPoints);
		seasonStanding.setGamesPlayed(gamesPlayed);
	}


}
