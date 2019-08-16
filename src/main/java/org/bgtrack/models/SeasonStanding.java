package org.bgtrack.models;

import java.io.Serializable;
import javax.persistence.*;

import org.bgtrack.models.user.Reguser;


/**
 * The persistent class for the season_standings database table.
 * 
 */
@Entity
@Table(name="season_standings")
@NamedQuery(name="SeasonStanding.findAll", query="SELECT s FROM SeasonStanding s")
public class SeasonStanding implements Serializable, Comparable<SeasonStanding> {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="SEASON_STANDING_ID")
	private int seasonStandingId;

	@Column(name="AVERAGED_POINTS")
	private double averagedPoints;

	private int place;

	@Column(name="TOTAL_POINTS")
	private double totalPoints;

	//bi-directional many-to-one association to Season
	@ManyToOne
	@JoinColumn(name="SEASON_ID")
	private Season season;

	//bi-directional many-to-one association to Reguser
	@ManyToOne
	@JoinColumn(name="USER_ID")
	private Reguser reguser;
	
	@Column(name="GAMES_PLAYED")
	private int gamesPlayed;

	public SeasonStanding() {
	}

	public int getSeasonStandingId() {
		return this.seasonStandingId;
	}

	public void setSeasonStandingId(int seasonStandingId) {
		this.seasonStandingId = seasonStandingId;
	}

	public double getAveragedPoints() {
		return this.averagedPoints;
	}

	public void setAveragedPoints(double averagedPoints) {
		this.averagedPoints = averagedPoints;
	}

	public int getPlace() {
		return this.place;
	}

	public void setPlace(int place) {
		this.place = place;
	}

	public double getTotalPoints() {
		return this.totalPoints;
	}

	public void setTotalPoints(double totalPoints) {
		this.totalPoints = totalPoints;
	}

	public Season getSeason() {
		return this.season;
	}

	public void setSeason(Season season) {
		this.season = season;
	}

	public Reguser getReguser() {
		return this.reguser;
	}

	public void setReguser(Reguser reguser) {
		this.reguser = reguser;
	}
	
	public int getGamesPlayed() {
		return this.gamesPlayed;
	}

	public void setGamesPlayed(int gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}

	public int compareTo(SeasonStanding comparedSeasonStanding) {
		if(this.averagedPoints > comparedSeasonStanding.averagedPoints)
	          return -1;
	    else if(comparedSeasonStanding.averagedPoints > this.averagedPoints)
	          return 1;
	    return 0;
	    
	}

}