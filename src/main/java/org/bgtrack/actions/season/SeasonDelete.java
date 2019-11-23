package org.bgtrack.actions.season;

import org.bgtrack.utils.BGTConstants;
import org.bgtrack.utils.HibernateUtil;

import java.util.List;

import org.apache.shiro.authz.AuthorizationException;
import org.bgtrack.auth.ShiroBaseAction;
import org.bgtrack.models.Season;
import org.bgtrack.models.daos.AuthorizationDAO;
import org.bgtrack.models.daos.SeasonDAO;
import org.bgtrack.models.user.authorization.Permission;

public class SeasonDelete extends ShiroBaseAction {
	private static final long serialVersionUID = -2322751648052461161L;
	
	private String seasonId;
	Season season;
	
	private String deleteSeasonPermissionValue;
	
	private boolean isSeasonDeleted = false;

	private static final String seasonDeletePermissionsErrorText = "Sorry, only the season creator may delete the season!";
	
	@Override
	public void validate() {
		
		super.validate();
		
		if (null == seasonId || seasonId.length() == 0) {
			
			addActionError(BGTConstants.checkFields);
			
			return;
			
		}
		
		this.season = SeasonDAO.getSeasonById(Integer.parseInt(seasonId), true);
		
		if (season == null) {
			
			addActionError(BGTConstants.checkFields);
			
			return;
			
		}
		
		deleteSeasonPermissionValue = "season:deleteseason:"+this.seasonId;
		
		if (!this.isExecutingUserPermitted(deleteSeasonPermissionValue)) {
			
			addActionError(seasonDeletePermissionsErrorText);
			
			throw new AuthorizationException("User: " + this.getShiroUser().getPrincipal() + " does not have the required "
					+ "permissions: "+ deleteSeasonPermissionValue + " for action: " + this.getClass() );
			
		}
		
	}
	
	public String execute() {

		HibernateUtil.deleteEntity(season);
		
		deleteAllPermissionsForSeason();

		setSeasonDeleted(true);
		
		return BGTConstants.success;
	}

	private void deleteAllPermissionsForSeason() {

		List<Permission> allPermissionsForSeason = AuthorizationDAO.getAllPermissionsForSeason(seasonId);
		
		for (Permission permissionForSeason : allPermissionsForSeason) {
			HibernateUtil.deleteEntity(permissionForSeason);
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

	// method must be named like this for struts to pick it up as a param
	public boolean getIsSeasonDeleted() {
		return isSeasonDeleted;
	}

	public void setSeasonDeleted(boolean isSeasonDeleted) {
		this.isSeasonDeleted = isSeasonDeleted;
	}

}
