package org.bgtrack.actions.season;

import org.bgtrack.models.ScoringType;

public class SeasonStandingHelperFactory {

	public static SeasonStandingHelper getSeasonStandingHelper(String seasonScoringType) {
		
		if (ScoringType.LAYERED.toString().equals(seasonScoringType)) {
			return new LayeredSeasonStandingHelper();
		} else {
			return new AveragedSeasonStandingHelper();
		}
		
	}
	
}
