package org.bgtrack.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bgtrack.models.Round;
import org.bgtrack.models.daos.RoundDAO;
import org.bgtrack.models.user.Reguser;

public class SeasonUtils {
	
	private static final Logger LOG = LogManager.getLogger(SeasonUtils.class);

	public static ArrayList<String> buildVictors(List<Round> rounds) {
		
		ArrayList<String> listofVictors = new ArrayList<String>();
		
		for (Round round : rounds) {
			List<Reguser> victors = RoundDAO.getVictorsForRound(round.getRoundId());
			
			if (victors.size() != 1) {
				
				StringBuilder tiedVictors = new StringBuilder();
				ListIterator<Reguser> iterator = victors.listIterator();
				
				while(iterator.hasNext()) {
					Reguser tempRegUser = iterator.next();
					tiedVictors.append(tempRegUser.getUsername()+" ");
					if (iterator.hasNext()) {
						tiedVictors.append("AND ");
					}
				}
				
				tiedVictors.append("(TIE)");
				listofVictors.add(tiedVictors.toString());
				
				LOG.debug("users {} tied for roundId {}", tiedVictors.toString(), round.getRoundId());
				
			} else {
				listofVictors.add(victors.get(0).getUsername());
				LOG.debug("user {} is victor for roundId {}", victors.get(0).getUsername(), round.getRoundId());
			}
		}
		
		return listofVictors;
		
	}
	
}
