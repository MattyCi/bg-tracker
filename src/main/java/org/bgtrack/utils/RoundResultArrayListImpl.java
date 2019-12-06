package org.bgtrack.utils;

import java.util.ArrayList;

import org.bgtrack.models.RoundResult;

public class RoundResultArrayListImpl extends ArrayList<RoundResult> implements RoundResultArrayList {
	private static final long serialVersionUID = -1609782469084847407L;

	public boolean doesRoundResultListContainPlace(int placeToSearch) {
		
		for (RoundResult roundResult : this) {
			
			if (roundResult.getPlace().intValue() == placeToSearch )
				return true;
			
		}
		
		return false;

	}
	
}
