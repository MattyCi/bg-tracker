package org.bgtrack.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.bgtrack.models.Round;
import org.bgtrack.models.daos.RoundDAO;
import org.bgtrack.models.user.Reguser;

public class SeasonUtils {

	public static ArrayList<String> buildVictors(List<Round> rounds) {
		
		ArrayList<String> listofVictors = new ArrayList<String>();
		
		for (Round round : rounds) {
			List<Reguser> victors = RoundDAO.getVictorsForRound(round.getRoundId());
			
			if (victors.size() != 1) {
				// we have a tie
				StringBuilder tiedVictors = new StringBuilder();
				ListIterator<Reguser> iterator = victors.listIterator();
				
				while(iterator.hasNext()) {
					Reguser tempRegUser = iterator.next();
					tiedVictors.append(tempRegUser.getFirstName()+" "+tempRegUser.getLastName()+" ");
					if (iterator.hasNext()) {
						tiedVictors.append("AND ");
					}
				}
				
				tiedVictors.append("(TIE)");
				listofVictors.add(tiedVictors.toString());
			} else {
				listofVictors.add(victors.get(0).getFirstName()+" "+victors.get(0).getLastName());
			}
		}
		
		return listofVictors;
		
	}
	
}
