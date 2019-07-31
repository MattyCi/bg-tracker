package org.bgtrack.models.user;

import java.io.Serializable;
import javax.persistence.*;

import org.bgtrack.models.RoundResult;

import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the regusers database table.
 * 
 */
@Entity
@Table(name="regusers")
@NamedQuery(name="Reguser.findAll", query="SELECT r FROM Reguser r")
public class Reguser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="USER_ID")
	private String userId;

	private String email;

	@Column(name="FIRST_NAME")
	private String firstName;

	@Column(name="LAST_NAME")
	private String lastName;

	private String password;

	@Column(name="REGISTRATION_TIME")
	private Timestamp registrationTime;

	private String salt;

	//bi-directional many-to-one association to RoundResult
	@OneToMany(mappedBy="reguser")
	private List<RoundResult> roundResults;

	public Reguser() {
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Timestamp getRegistrationTime() {
		return this.registrationTime;
	}

	public void setRegistrationTime(Timestamp registrationTime) {
		this.registrationTime = registrationTime;
	}

	public String getSalt() {
		return this.salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public List<RoundResult> getRoundResults() {
		return this.roundResults;
	}

	public void setRoundResults(List<RoundResult> roundResults) {
		this.roundResults = roundResults;
	}

	public RoundResult addRoundResult(RoundResult roundResult) {
		getRoundResults().add(roundResult);
		roundResult.setReguser(this);

		return roundResult;
	}

	public RoundResult removeRoundResult(RoundResult roundResult) {
		getRoundResults().remove(roundResult);
		roundResult.setReguser(null);

		return roundResult;
	}

}