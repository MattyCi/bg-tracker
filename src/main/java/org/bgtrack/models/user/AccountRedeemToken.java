package org.bgtrack.models.user;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.*;


/**
 * The persistent class for the account_redeem_tokens database table.
 * 
 */
@Entity
@Table(name="account_redeem_tokens")
@NamedQuery(name="AccountRedeemToken.findAll", query="SELECT a FROM AccountRedeemToken a")
public class AccountRedeemToken implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="USER_ID")
	private String userId;

	@Column(name="REDEEM_TOKEN")
	private String redeemToken;
	
	@Column(name="DATE_CREATED")
	private Timestamp dateCreated;

	//bi-directional one-to-one association to Reguser
	@OneToOne
	@JoinColumn(name="USER_ID")
	private Reguser reguser;
	
	//bi-directional many-to-one association to Reguser
	@ManyToOne
	@JoinColumn(name="CREATOR")
	private Reguser creator;

	public AccountRedeemToken() {
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRedeemToken() {
		return this.redeemToken;
	}

	public void setRedeemToken(String redeemToken) {
		this.redeemToken = redeemToken;
	}

	public Timestamp getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Timestamp dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public Reguser getReguser() {
		return this.reguser;
	}

	public void setReguser(Reguser reguser) {
		this.reguser = reguser;
	}
	
	public Reguser getCreator() {
		return this.creator;
	}

	public void setCreator(Reguser creator) {
		this.creator = creator;
	}

}