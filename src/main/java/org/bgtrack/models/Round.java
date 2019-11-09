package org.bgtrack.models;

import java.io.Serializable;
import javax.persistence.*;

import org.bgtrack.models.user.Reguser;
import org.hibernate.annotations.OrderBy;

import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the rounds database table.
 * 
 */
@Entity
@Table(name="rounds")
@NamedQuery(name="Round.findAll", query="SELECT r FROM Round r")
public class Round implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ROUND_ID")
	private int roundId;

	@Column(name="ROUND_DATE")
	private Timestamp roundDate;

	//bi-directional many-to-one association to RoundResult
	@OneToMany(mappedBy="round")
	@org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.ALL)
	@OrderBy(clause = "PLACE ASC")
	private List<RoundResult> roundResults;

	//bi-directional many-to-one association to Season
	@ManyToOne
	@JoinColumn(name="SEASON_ID")
	private Season season;
	
	//bi-directional many-to-one association to Reguser
	@ManyToOne
	@JoinColumn(name="CREATOR")
	private Reguser creator;

	public Round() {
	}

	public int getRoundId() {
		return this.roundId;
	}

	public void setRoundId(int roundId) {
		this.roundId = roundId;
	}

	public Timestamp getRoundDate() {
		return this.roundDate;
	}

	public void setRoundDate(Timestamp roundDate) {
		this.roundDate = roundDate;
	}

	public List<RoundResult> getRoundResults() {
		return this.roundResults;
	}

	public void setRoundResults(List<RoundResult> roundResults) {
		this.roundResults = roundResults;
	}

	public RoundResult addRoundResult(RoundResult roundResult) {
		getRoundResults().add(roundResult);
		roundResult.setRound(this);

		return roundResult;
	}

	public RoundResult removeRoundResult(RoundResult roundResult) {
		getRoundResults().remove(roundResult);
		roundResult.setRound(null);

		return roundResult;
	}

	public Season getSeason() {
		return this.season;
	}

	public void setSeason(Season season) {
		this.season = season;
	}
	
	public Reguser getCreator() {
		return this.creator;
	}

	public void setCreator(Reguser creator) {
		this.creator = creator;
	}

}