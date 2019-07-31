package org.bgtrack.actions.season;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import net.bytebuddy.asm.Advice.This;

import org.bgtrack.utils.BGTConstants;

import org.bgtrack.utils.HibernateUtil;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;
import org.apache.struts2.interceptor.HttpParametersAware;
import org.apache.struts2.interceptor.ParameterAware;
import org.bgtrack.auth.ShiroBaseAction;
import org.bgtrack.models.Round;
import org.bgtrack.models.RoundResult;
import org.bgtrack.models.Season;
import org.bgtrack.models.daos.GameDAO;
import org.bgtrack.models.daos.SeasonDAO;
import org.bgtrack.models.user.Reguser;
import org.bgtrack.models.user.daos.UserDAO;

public class RoundCreate extends ShiroBaseAction implements HttpParametersAware {
	private static final long serialVersionUID = -6328260956217475993L;
	private String seasonName;
	private String seasonId;
	private String seasonEndDate;
	private boolean errorsOccured = false;
	private HttpParameters parameters;
	Season season;
	Round round;
	List<RoundResult> roundResultList;

	public String execute() {
		
		if (!this.shiroUser.isAuthenticated()) {
			addActionError(BGTConstants.authenticationError);
			return BGTConstants.error;
		}
		
		if (!this.shiroUser.getPrincipal().toString().equals("matt@test.com")) {
			addActionError(BGTConstants.authorizationError);
			return BGTConstants.error;
		}
		
		if (null == seasonId || seasonId.length() == 0) {
			addActionError(BGTConstants.checkFields);
			return BGTConstants.error;
		} else {
			this.season = SeasonDAO.getSeasonById(Integer.parseInt(seasonId), false);
			if (season == null) {
				addActionError(BGTConstants.checkFields);
				return BGTConstants.error;
			}
		}

		this.roundResultList = new ArrayList<RoundResult>(); 
		
		Round round = new Round();
		Timestamp roundStartTimestamp = new Timestamp(System.currentTimeMillis());
		round.setRoundDate(roundStartTimestamp);
		round.setSeason(this.season);
		
		// 12 max players
		for (int i = 0; i < 13; i++ ) {
			Parameter userId;
			if (this.getParameters().contains("roundPlayer"+i)) {
				userId = this.getParameters().get("roundPlayer"+i);
			} else {
				break;
			}
			
			BigInteger userPlace = new BigInteger(this.getParameters().get("playerPlace"+i).toString());
			
			Reguser roundUser = UserDAO.getUserByID(userId.toString());
			
			if (null != roundUser) {
				buildRoundResultList(roundUser, userPlace, round);
			}

		}
		
		round.setRoundResults(this.roundResultList);
		
		HibernateUtil.persistObject(round);
		
		if (errorsOccured) {
			return BGTConstants.error;
		}

		return BGTConstants.success;
	}

	private void buildRoundResultList(Reguser roundUser, BigInteger userPlace, Round round) {
		RoundResult roundResult = new RoundResult();
		roundResult.setRound(round);
		roundResult.setReguser(roundUser);
		roundResult.setPlace(userPlace);
		this.roundResultList.add(roundResult);
	}

	public String getSeasonName() {
		return seasonName;
	}

	public void setSeasonName(String seasonName) {
		this.seasonName = seasonName;
	}

	public String getSeasonId() {
		return seasonId;
	}

	public void setSeasonId(String seasonId) {
		this.seasonId = seasonId;
	}

	public String getSeasonEndDate() {
		return seasonEndDate;
	}

	public void setSeasonEndDate(String seasonEndDate) {
		this.seasonEndDate = seasonEndDate;
	}

	public HttpParameters getParameters() {
		return this.parameters;
	}

	public void setParameters(HttpParameters parameters) {
		this.parameters = parameters;
	}
	
	public Season getSeason() {
		return season;
	}

	public void setSeason(Season season) {
		this.season = season;
	}

	public List<RoundResult> getRoundResultList() {
		return roundResultList;
	}

	public void setRoundResultList(List<RoundResult> roundResultList) {
		this.roundResultList = roundResultList;
	}

	public Round getRound() {
		return round;
	}

	public void setRound(Round round) {
		this.round = round;
	}

}
