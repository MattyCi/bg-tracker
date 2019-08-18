package org.bgtrack.actions.season;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.bgtrack.utils.BGTConstants;

import org.bgtrack.utils.HibernateUtil;
import org.bgtrack.auth.ShiroBaseAction;
import org.bgtrack.models.Season;
import org.bgtrack.models.daos.GameDAO;

public class SeasonCreate extends ShiroBaseAction {
	private static final long serialVersionUID = -6328260956217475993L;
	private String seasonName;
	private String seasonGameId;
	private String seasonEndDate;
	private String seasonScoringType;
	private boolean errorsOccured = false;
	
	public String execute() {
		
		if (!this.shiroUser.isAuthenticated()) {
			addActionError(BGTConstants.authenticationError);
			return BGTConstants.error;
		}
		
		if (!this.shiroUser.getPrincipal().toString().equals("matt@test.com")) {
			addActionError(BGTConstants.authorizationError);
			return BGTConstants.error;
		}
		
		if (seasonName.isEmpty() || seasonGameId.isEmpty() || seasonEndDate.isEmpty() || seasonName.length() == 0 || 
				seasonGameId.length() == 0 || seasonEndDate.length() == 0) {
			addActionError(BGTConstants.checkFields);
			return BGTConstants.error;
		}
		
		createSeason(seasonName, Integer.parseInt(seasonGameId), seasonEndDate);

		if (errorsOccured) {
			return BGTConstants.error;
		}

		return BGTConstants.success;
	}

	public void createSeason(String seasonName, int seasonGameId, String seasonEndDate) {
		Timestamp seasonStartTimestamp = new Timestamp(System.currentTimeMillis());
		Timestamp seasonEndTimestamp = null;
		
		try {
		    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy");
		    Date parsedDate = dateFormat.parse(seasonEndDate);
		    seasonEndTimestamp = new java.sql.Timestamp(parsedDate.getTime());
		} catch(Exception e) {
			addActionError(BGTConstants.dateError);
			errorsOccured = true;
		}
		
		Season season = new Season();
		season.setName(seasonName);
		season.setGame(GameDAO.getGameById(seasonGameId));
		season.setStartDate(seasonStartTimestamp);
		season.setEndDate(seasonEndTimestamp);
		season.setScoringType(seasonScoringType);

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			session.save(season);
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			System.err.println("Hibernate error occured: "+e);
			errorsOccured = true;
			throw e;
		} finally {
			session.close();
		}
	}

	public String getSeasonName() {
		return seasonName;
	}

	public void setSeasonName(String seasonName) {
		this.seasonName = seasonName;
	}

	public String getSeasonGameId() {
		return seasonGameId;
	}

	public void setSeasonGameId(String seasonGameId) {
		this.seasonGameId = seasonGameId;
	}

	public String getSeasonEndDate() {
		return seasonEndDate;
	}

	public void setSeasonEndDate(String seasonEndDate) {
		this.seasonEndDate = seasonEndDate;
	}

	public String getSeasonScoringType() {
		return seasonScoringType;
	}

	public void setSeasonScoringType(String seasonScoringType) {
		this.seasonScoringType = seasonScoringType;
	}

}
