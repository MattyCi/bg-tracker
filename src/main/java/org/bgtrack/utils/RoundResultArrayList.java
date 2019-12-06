package org.bgtrack.utils;

import java.util.List;

import org.bgtrack.models.RoundResult;

public interface RoundResultArrayList extends List<RoundResult> {

	boolean doesRoundResultListContainPlace(int placeToSearch);
	
}
