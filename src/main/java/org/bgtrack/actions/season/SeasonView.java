package org.bgtrack.actions.season;

import org.bgtrack.utils.BGTConstants;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.bgtrack.auth.ShiroBaseAction;
import org.bgtrack.models.Round;
import org.bgtrack.models.RoundResult;
import org.bgtrack.models.Season;
import org.bgtrack.models.daos.RoundDAO;
import org.bgtrack.models.daos.SeasonDAO;
import org.bgtrack.models.user.Reguser;

public class SeasonView extends ShiroBaseAction {
	private static final long serialVersionUID = -6328260956217475993L;
	private String seasonId;
	private Season season;
	private boolean errorsOccured = false;
	private List<String> listofVictors;
	private boolean seasonStatus;
	
	public String execute() {
		
		if (!this.shiroUser.isAuthenticated()) {
			addActionError(BGTConstants.authenticationError);
			return BGTConstants.error;
		}
		
		if (seasonId.isEmpty() || seasonId.length() == 0) {
			addActionError(BGTConstants.seasonIdError);
			return BGTConstants.error;
		}
		
		try {
			this.setSeason(SeasonDAO.getSeasonById(Integer.parseInt(seasonId), true));
		} catch (NumberFormatException e) {
			addActionError(BGTConstants.seasonIdError);
			return BGTConstants.error; 
		}
		
		buildVictors(this.getSeason().getRounds());
		
		determineSeasonStatus();
		
		if (errorsOccured) {
			return BGTConstants.error;
		}

		return BGTConstants.success;
	}

	private void buildVictors(List<Round> rounds) {
		this.setListofVictors(new ArrayList<String>());
		
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
				this.getListofVictors().add(tiedVictors.toString());
			} else {
				this.getListofVictors().add(victors.get(0).getFirstName()+" "+victors.get(0).getLastName());
			}
		}
	}
	
	private void determineSeasonStatus() {
		
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		
		System.out.println("CURRENT TIME: "+currentTime);
		System.out.println("END TIME: "+this.getSeason().getEndDate());
		
		if(currentTime.after(this.getSeason().getEndDate())) {
			
			this.setSeasonStatus(false);
			
		} else {
			this.setSeasonStatus(true);
		}
		
	}

	public String getSeasonId() {
		return seasonId;
	}

	public void setSeasonId(String seasonId) {
		this.seasonId = seasonId;
	}

	public Season getSeason() {
		return season;
	}

	public void setSeason(Season season) {
		this.season = season;
	}

	public List<String> getListofVictors() {
		return listofVictors;
	}

	public void setListofVictors(List<String> listofVictors) {
		this.listofVictors = listofVictors;
	}

	public boolean getSeasonStatus() {
		return seasonStatus;
	}

	public void setSeasonStatus(boolean seasonStatus) {
		this.seasonStatus = seasonStatus;
	}
	
}
