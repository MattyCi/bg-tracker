package org.bgtrack.models;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.OrderBy;

import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the season database table.
 * 
 */
@Entity
@NamedQuery(name="Season.findAll", query="SELECT s FROM Season s")
public class Season implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="SEASON_ID")
	private String seasonId;

	@Column(name="END_DATE")
	private Timestamp endDate;

	private String name;

	@Column(name="START_DATE")
	private Timestamp startDate;

	//bi-directional many-to-one association to Round
	@OneToMany(mappedBy="season")
	@OrderBy(clause = "ROUND_DATE DESC")
	private List<Round> rounds;

	//bi-directional many-to-one association to Game
	@ManyToOne
	@JoinColumn(name="GAME_ID")
	private Game game;

	public Season() {
	}

	public String getSeasonId() {
		return this.seasonId;
	}

	public void setSeasonId(String seasonId) {
		this.seasonId = seasonId;
	}

	public Timestamp getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public List<Round> getRounds() {
		return this.rounds;
	}

	public void setRounds(List<Round> rounds) {
		this.rounds = rounds;
	}

	public Round addRound(Round round) {
		getRounds().add(round);
		round.setSeason(this);

		return round;
	}

	public Round removeRound(Round round) {
		getRounds().remove(round);
		round.setSeason(null);

		return round;
	}

	public Game getGame() {
		return this.game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

}