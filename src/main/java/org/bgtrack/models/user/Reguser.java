package org.bgtrack.models.user;

import java.io.Serializable;
import javax.persistence.*;

import org.bgtrack.models.user.AccountRedeemToken;
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

	private String username;

	private String password;

	@Column(name="REGISTRATION_TIME")
	private Timestamp registrationTime;

	private String salt;

	//bi-directional many-to-one association to RoundResult
	@OneToMany(mappedBy="reguser")
	private List<RoundResult> roundResults;
	
	//bi-directional one-to-one association to AccountRedeemToken
	@OneToOne(mappedBy="reguser")
	private AccountRedeemToken accountRedeemToken;
	
	//bi-directional many-to-one association to AccountRedeemToken
	@OneToMany(mappedBy="creator")
	private List<AccountRedeemToken> createdAccountRedeemTokens;

	public Reguser() {
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
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
	
	public AccountRedeemToken getAccountRedeemToken() {
		return this.accountRedeemToken;
	}

	public void setAccountRedeemToken(AccountRedeemToken accountRedeemToken) {
		this.accountRedeemToken = accountRedeemToken;
	}
	
	public List<AccountRedeemToken> getCreatedAccountRedeemTokens() {
		return this.createdAccountRedeemTokens;
	}

	public void setCreatedAccountRedeemTokens(List<AccountRedeemToken> createdAccountRedeemTokens) {
		this.createdAccountRedeemTokens = createdAccountRedeemTokens;
	}

	public AccountRedeemToken addCreatedAccountRedeemToken(AccountRedeemToken accountRedeemToken) {
		getCreatedAccountRedeemTokens().add(accountRedeemToken);
		accountRedeemToken.setCreator(this);

		return accountRedeemToken;
	}

	public AccountRedeemToken removeCreatedAccountRedeemToken(AccountRedeemToken accountRedeemToken) {
		getCreatedAccountRedeemTokens().remove(accountRedeemToken);
		accountRedeemToken.setCreator(null);

		return accountRedeemToken;
	}

}