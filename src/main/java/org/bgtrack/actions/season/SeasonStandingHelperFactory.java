package org.bgtrack.actions.season;

public class SeasonStandingHelperFactory {

	public static SeasonStandingHelper getSeasonStandingHelper(String seasonScoringTypes) {
		
		if (seasonScoringTypes.equals("L")) {
			return new LayeredSeasonStandingHelper();
		} else {
			return new AveragedSeasonStandingHelper();
		}
		
	}
	
}
