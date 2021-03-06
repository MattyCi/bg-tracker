package org.bgtrack.actions.season;

import org.bgtrack.utils.BGTConstants;
import org.bgtrack.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authz.AuthorizationException;
import org.bgtrack.auth.ShiroBaseAction;
import org.bgtrack.models.Season;
import org.bgtrack.models.daos.SeasonDAO;

public class SeasonEdit extends ShiroBaseAction {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(SeasonEdit.class);
	
	private String seasonId;
	private String seasonEndDate;
	private String seasonName;
	
	Season season;
	
	private String editSeasonPermissionValue;
			
	private Boolean errorsOccurred = false;
	
	private static final String SEASON_EDIT_PERMISSIONS_ERROR_TEXT = "Sorry, only a season admin may edit the season!";
	private static final String INVALID_END_DATE_ERROR_TEXT = "The season end date provided is earlier than the season start date, please choose a valid date and try again.";
	private static final String GENERIC_SEASON_UPDATE_ERROR_TEXT = "Sorry... something went wrong and we were unable to update the season data.";
	private static final String SEASON_EDIT_CONFIRMATION_TEXT = "Done! Season edited successfully.";
	
	private static final String SEASON_NAME_EXISTS_ERROR_TEXT = "Sorry, but the season name you provided already exists.";
	
	@Override
	public Boolean isCsrfProtected() {
		return true;
	}
	
	@Override
	public void validate() {
		
		super.validate();
		
		if (null == seasonId || seasonId.length() == 0) {
			
			LOG.debug("user: {} gave invalid seasonId", shiroUser.getPrincipal());
			
			addActionError(BGTConstants.CHECK_FIELDS);
			
			return;
			
		}
		
		this.season = SeasonDAO.getSeasonById(Integer.parseInt(seasonId), true);
		
		if (season == null) {
			
			LOG.info("user {} trying to edit season but unable to find seasonId {} in database: " + shiroUser.getPrincipal(), seasonId);
			
			addActionError(BGTConstants.CHECK_FIELDS);
			
			return;
			
		}
		
		editSeasonPermissionValue = "season:editseason:"+this.seasonId;
		
		if (!this.isExecutingUserPermitted(editSeasonPermissionValue)) {           
			
			LOG.info("user {} trying to edit season but does not have permission: " + shiroUser.getPrincipal());
			
			addActionError(SEASON_EDIT_PERMISSIONS_ERROR_TEXT);
			
			throw new AuthorizationException("User: " + this.getShiroUser().getPrincipal() + " does not have the required "
					+ "permissions: "+ editSeasonPermissionValue + " for action: " + this.getClass() );
			
		}
		
	}
	
	public String execute() {

		try {
						
			if (seasonEndDate != null && !seasonEndDate.isEmpty()) {
				changeSeasonEndDate();
			}
			
		} catch (ParseException e) {
			
			LOG.info("user {} trying to edit season but gave invalid date of {}: ", shiroUser.getPrincipal(), seasonEndDate);
			
			addActionError(BGTConstants.DATE_ERROR);
			return ERROR;
			
		} catch (Exception e) {
			
			LOG.error("Error updating hibernate season entity. NAME: " + this.season.getName() + " ID: " + this.season.getSeasonId());
			addActionError(GENERIC_SEASON_UPDATE_ERROR_TEXT);
			return ERROR;
			
		}
		
		if (seasonName != null && !seasonName.isEmpty()) {
			changeSeasonName();
		}
		
		if (errorsOccurred)
			return ERROR;
		
		setPopupMessage(SEASON_EDIT_CONFIRMATION_TEXT);
		
		return SUCCESS;
	}

	// TODO: move this operation into a season facade... duplicate code exists in season create now
	private void changeSeasonEndDate() throws Exception {
		
	    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy");
	    Date parsedDate = dateFormat.parse(seasonEndDate);
	    
	    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
	    Timestamp seasonEndTimestamp = new java.sql.Timestamp(parsedDate.getTime());
	    
	    if (seasonEndTimestamp.before(season.getStartDate())) {
	    	
	    	addActionError(INVALID_END_DATE_ERROR_TEXT);
	    	errorsOccurred = true;
	    	return;
	    	
	    } else if (seasonEndTimestamp.before(currentTime)) {
	    	
	    	season.setStatus("I");
	    	season.setEndDate(seasonEndTimestamp);
	    	
	    } else {
	    	
	    	season.setStatus("A");
	    	season.setEndDate(seasonEndTimestamp);
	    	
	    }
	    
		HibernateUtil.updateObject(season);
		
	}
	
	private void changeSeasonName() {
		
		this.season.setName(seasonName);
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			session.update(season);
			tx.commit();
		} catch (Exception e) {
			
			if (e.getCause() instanceof ConstraintViolationException) {
				
				LOG.info("User {} tried creating a season with name {}, but name already exists. {}", shiroUser.getPrincipal(), season.getName(), e.getMessage());
				addActionError(SEASON_NAME_EXISTS_ERROR_TEXT);
				
			}
			
			tx.rollback();

			errorsOccurred = true;
			
			return;
			
		} finally {
			session.close();
		}
		
	}

	public String getSeasonId() {
		return seasonId;
	}

	public void setSeasonId(String seasonId) {
		this.seasonId = seasonId;
	}
	
	public Season getSeason() {
		return season;
	}

	public void setSeason(Season season) {
		this.season = season;
	}

	public String getSeasonEndDate() {
		return seasonEndDate;
	}

	public void setSeasonEndDate(String seasonEndDate) {
		this.seasonEndDate = seasonEndDate;
	}

	public String getSeasonName() {
		return seasonName;
	}

	public void setSeasonName(String seasonName) {
		this.seasonName = seasonName;
	}

}
