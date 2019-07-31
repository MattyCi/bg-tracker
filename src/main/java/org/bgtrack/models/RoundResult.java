package org.bgtrack.models;

import java.io.Serializable;
import javax.persistence.*;

import org.bgtrack.models.user.Reguser;

import java.math.BigInteger;


/**
 * The persistent class for the round_results database table.
 * 
 */
@Entity
@Table(name="round_results")
@NamedQuery(name="RoundResult.findAll", query="SELECT r FROM RoundResult r")
public class RoundResult implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ROUND_RESULTS_ID")
	private String roundResultsId;

	private BigInteger place;

	//bi-directional many-to-one association to Reguser
	@ManyToOne
	@JoinColumn(name="USER_ID")
	private Reguser reguser;

	//bi-directional many-to-one association to Round
	@ManyToOne
	@JoinColumn(name="ROUND_ID")
	private Round round;

	public RoundResult() {
	}

	public String getRoundResultsId() {
		return this.roundResultsId;
	}

	public void setRoundResultsId(String roundResultsId) {
		this.roundResultsId = roundResultsId;
	}

	public BigInteger getPlace() {
		return this.place;
	}

	public void setPlace(BigInteger place) {
		this.place = place;
	}

	public Reguser getReguser() {
		return this.reguser;
	}

	public void setReguser(Reguser reguser) {
		this.reguser = reguser;
	}

	public Round getRound() {
		return this.round;
	}

	public void setRound(Round round) {
		this.round = round;
	}

}