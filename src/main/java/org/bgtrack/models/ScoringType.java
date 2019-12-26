package org.bgtrack.models;

public enum ScoringType {

	AVERAGED("A"), HANDICAPPED("H");

	private final String scoringType;

	ScoringType(final String scoringType) {
		this.scoringType = scoringType;
	}

	@Override
	public String toString() {
		return scoringType;
	}

}
