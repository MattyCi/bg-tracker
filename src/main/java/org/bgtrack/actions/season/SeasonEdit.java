package org.bgtrack.actions.season;

import org.bgtrack.utils.BGTConstants;
import org.bgtrack.utils.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.shiro.authz.AuthorizationException;
import org.bgtrack.auth.BGTrackRealm;
import org.bgtrack.auth.ShiroBaseAction;
import org.bgtrack.models.Season;
import org.bgtrack.models.daos.AuthorizationDAO;
import org.bgtrack.models.daos.SeasonDAO;
import org.bgtrack.models.user.authorization.Permission;

public class SeasonEdit extends ShiroBaseAction {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(BGTrackRealm.class);
	
	private String seasonId;
	private String seasonEndDate;
	
	Season season;
	
	private String editSeasonPermissionValue;
			
	private Boolean errorsOccurred = false;
	
	private static final String SEASON_EDIT_PERMISSIONS_ERROR_TEXT = "Sorry, only a season admin may edit the season!";
	private static final String INVALID_END_DATE_ERROR_TEXT = "The season end date provided is earlier than the season start date, please choose a valid date and try again.";
	private static final String GENERIC_SEASON_UPDATE_ERROR_TEXT = "Sorry... something went wrong and we were unable to update the season data.";
	private static final String SEASON_EDIT_CONFIRMATION_TEXT = "Done! Season edited successfully.";
	
	@Override
	public Boolean isCsrfProtected() {
		return true;
	}
	
	@Override
	public void validate() {
		
		super.validate();
		
		if (null == seasonId || seasonId.length() == 0) {
			
			addActionError(BGTConstants.CHECK_FIELDS);
			
			return;
			
		}
		
		this.season = SeasonDAO.getSeasonById(Integer.parseInt(seasonId), true);
		
		if (season == null) {
			
			addActionError(BGTConstants.CHECK_FIELDS);
			
			return;
			
		}
		
		editSeasonPermissionValue = "season:editseason:"+this.seasonId;
		
		if (!this.isExecutingUserPermitted(editSeasonPermissionValue)) {           
			
			addActionError(SEASON_EDIT_PERMISSIONS_ERROR_TEXT);
			
			throw new AuthorizationException("User: " + this.getShiroUser().getPrincipal() + " does not have the required "
					+ "permissions: "+ editSeasonPermissionValue + " for action: " + this.getClass() );
			
		}
		
	}
	
	public String execute() {

		try {
			
			if (seasonEndDate != null) {
				changeSeasonEndDate();
			}
			
		} catch (ParseException e) {
			
			addActionError(BGTConstants.DATE_ERROR);
			return ERROR;
			
		} catch (Exception e) {
			
			LOG.error("Error updating hibernate season entity. NAME: " + this.season.getName() + " ID: " + this.season.getSeasonId());
			addActionError(GENERIC_SEASON_UPDATE_ERROR_TEXT);
			return ERROR;
			
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

}
