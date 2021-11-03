package org.bgtrack.actions.season;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bgtrack.auth.SeasonUtils;
import org.bgtrack.models.Round;
import org.bgtrack.models.RoundResult;
import org.bgtrack.models.Season;
import org.bgtrack.models.SeasonStanding;
import org.bgtrack.models.daos.RoundDAO;
import org.bgtrack.models.daos.SeasonDAO;
import org.bgtrack.models.user.Reguser;

public class UserSeasonStats {
	private List<SeasonStanding> seasonStandingList;

	int roundPage;
	private List<Round> paginatedRounds;
	private List<Round> roundsUserParticipatedIn;
	
	private List<String> listofVictors;

	Season season;
	Reguser selectedUser;
	
	private double winRatio;

	private HashMap<BigInteger, Integer> placeOccurances;

	public UserSeasonStats(Season selectedSeason, Reguser selectedUser, int roundPage) throws NumberFormatException, IOException {
		
		this.setSeason(selectedSeason);
		this.setSelectedUser(selectedUser);
		this.setRoundPage(roundPage);
		
		setSeasonStanding();
		
		buildPaginatedRoundList();
		
		buildPlaceFinishes();
		
		determineWinRatio();
		
		this.setListofVictors(SeasonUtils.buildVictors(paginatedRounds));
	}

	private void setSeasonStanding() {
		this.seasonStandingList = SeasonDAO.getSeasonStandingForUserInSeason(season.getSeasonId(), selectedUser.getUserId());
	}
	
	private void buildPaginatedRoundList() throws NumberFormatException, IOException {
		
		this.paginatedRounds = new ArrayList<Round>();
		
		this.paginatedRounds = RoundDAO.getRoundsForUserBySeasonId(season.getSeasonId(), selectedUser.getUserId(), roundPage);
		
	}
	
	private void buildPlaceFinishes() throws NumberFormatException, IOException {
		
		this.roundsUserParticipatedIn = new ArrayList<Round>();
		this.roundsUserParticipatedIn = RoundDAO.getRoundsForUserBySeasonId(season.getSeasonId(), selectedUser.getUserId(), -1);
		
		extractPlaceFinishesFromRoundResults();

	}

	private void extractPlaceFinishesFromRoundResults() {
		
		this.placeOccurances = new HashMap<BigInteger, Integer>(roundsUserParticipatedIn.size());
		
		for (int i = 0; i < roundsUserParticipatedIn.size(); i++) {
			insertPlaceOccuranceIntoHashMap(roundsUserParticipatedIn.get(i));
		}
		
	}

	private void insertPlaceOccuranceIntoHashMap(Round round) {
		
		RoundResult usersRoundResultFromRound = determineUsersRoundResult(round);
		BigInteger place;
		
		if (usersRoundResultFromRound != null) {
			place = usersRoundResultFromRound.getPlace();
		} else {
			place = BigInteger.valueOf(-999);
		}
		
		if (this.placeOccurances.get(place) == null) {
			this.placeOccurances.put(place, 1);
		} else {
			Integer previousPlaceOccurances = placeOccurances.get(place);
			this.placeOccurances.put(place, previousPlaceOccurances+1);
		}
		
	}
	
	private RoundResult determineUsersRoundResult(Round round) {
		
		for (RoundResult roundResult : round.getRoundResults()) {
			if (isUserInRoundResult(roundResult)) {
				return roundResult;
			}
		}
		
		return null;
	}

	private boolean isUserInRoundResult(RoundResult roundResult) {
		
		if (selectedUser.getUserId().equals(roundResult.getReguser().getUserId())) {
			return true;
		}
		
		return false;
	}

	private void determineWinRatio() {
		
		if (placeOccurances.get(BigInteger.valueOf(1)) != null) {
			this.winRatio = (placeOccurances.get(BigInteger.valueOf(1)) / new Double(roundsUserParticipatedIn.size())) * 100;
			DecimalFormat df = new DecimalFormat("#.##");
			this.winRatio = Double.valueOf(df.format(this.winRatio));
		} else {
			this.winRatio = 0.00;
		}
		
	}

	public Season getSeason() {
		return season;
	}

	public void setSeason(Season selectedSeason) {
		this.season = selectedSeason;
	}
	
	public List<SeasonStanding> getSeasonStandingList() {
		return seasonStandingList;
	}

	public void setSeasonStandingList(List<SeasonStanding> seasonStandingList) {
		this.seasonStandingList = seasonStandingList;
	}

	public Reguser getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(Reguser selectedUser) {
		this.selectedUser = selectedUser;
	}
	
	public double getWinRatio() {
		return winRatio;
	}

	public void setWinRatio(double winRatio) {
		this.winRatio = winRatio;
	}
	
	public HashMap<BigInteger, Integer> getPlaceOccurances() {
		return placeOccurances;
	}

	public void setPlaceOccurances(HashMap<BigInteger, Integer> placeOccurances) {
		this.placeOccurances = placeOccurances;
	}
	
	public List<Round> getRoundsUserParticipatedIn() {
		return roundsUserParticipatedIn;
	}

	public void setRoundsUserParticipatedIn(List<Round> rounds) {
		this.roundsUserParticipatedIn = rounds;
	}
	
	public List<String> getListofVictors() {
		return listofVictors;
	}

	public void setListofVictors(List<String> listofVictors) {
		this.listofVictors = listofVictors;
	}
	
	public int getRoundPage() {
		return roundPage;
	}

	public void setRoundPage(int roundPage) {
		this.roundPage = roundPage;
	}

	public List<Round> getPaginatedRounds() {
		return paginatedRounds;
	}

	public void setPaginatedRounds(List<Round> paginatedRounds) {
		this.paginatedRounds = paginatedRounds;
	}
	
}
