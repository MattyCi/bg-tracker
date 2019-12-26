package org.bgtrack.actions.season;

import org.bgtrack.models.ScoringType;

public class SeasonStandingHelperFactory {

	public static SeasonStandingHelper getSeasonStandingHelper(String seasonScoringType) {
		
		if (ScoringType.HANDICAPPED.toString().equals(seasonScoringType)) {
			return new HandicappedSeasonStandingHelper();
		} else {
			return new AveragedSeasonStandingHelper();
		}
		
	}
	
}
