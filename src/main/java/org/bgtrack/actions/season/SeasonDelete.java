package org.bgtrack.actions.season;

import org.bgtrack.utils.BGTConstants;
import org.bgtrack.utils.HibernateUtil;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authz.AuthorizationException;
import org.bgtrack.auth.ShiroBaseAction;
import org.bgtrack.models.Season;
import org.bgtrack.models.daos.AuthorizationDAO;
import org.bgtrack.models.daos.SeasonDAO;
import org.bgtrack.models.user.authorization.Permission;

public class SeasonDelete extends ShiroBaseAction {
	private static final long serialVersionUID = -2322751648052461161L;
	
	private static final Logger LOG = LogManager.getLogger(SeasonDelete.class);
	
	private String seasonId;
	Season season;
	
	private String deleteSeasonPermissionValue;
	
	private static final String SEASON_DELETE_PERMISSIONS_ERROR_TEXT = "Sorry, only the season creator may delete the season!";
	private static final String SEASON_DELETE_CONFIRMATION_TEXT = "Done! Season deleted successfully.";
	
	@Override
	public Boolean isCsrfProtected() {
		return true;
	}
	
	@Override
	public void validate() {
		
		super.validate();
		
		if (null == seasonId || seasonId.length() == 0) {
			
			LOG.info("user trying to delete season but no seasonId was given: " + shiroUser.getPrincipal());
			
			addActionError(BGTConstants.CHECK_FIELDS);
			
			return;
			
		}
		
		this.season = SeasonDAO.getSeasonById(Integer.parseInt(seasonId), true);
		
		if (season == null) {
			
			LOG.info("user {} trying to delete season but unable to find seasonId {} in database: " + shiroUser.getPrincipal(), seasonId);
			
			addActionError(BGTConstants.CHECK_FIELDS);
			
			return;
			
		}
		
		deleteSeasonPermissionValue = "season:deleteseason:"+this.seasonId;

		if (!this.isExecutingUserPermitted(deleteSeasonPermissionValue)) {
			
			LOG.info("user {} trying to delete season but does not have rights for seasonId {}", shiroUser.getPrincipal() ,seasonId);
			
			addActionError(SEASON_DELETE_PERMISSIONS_ERROR_TEXT);
			
			throw new AuthorizationException("User: " + this.getShiroUser().getPrincipal() + " does not have the required "
					+ "permissions: "+ deleteSeasonPermissionValue + " for action: " + this.getClass() );
			
		}
		
	}
	
	public String execute() {

		HibernateUtil.deleteEntity(season);
		
		deleteAllPermissionsForSeason();

		setPopupMessage(SEASON_DELETE_CONFIRMATION_TEXT);
		
		return BGTConstants.SUCCESS;
	}

	private void deleteAllPermissionsForSeason() {

		List<Permission> allPermissionsForSeason = AuthorizationDAO.getAllPermissionsForSeason(seasonId);
		
		for (Permission permissionForSeason : allPermissionsForSeason) {
			
			LOG.debug("deleting permission: {} for seasonId: {}", permissionForSeason.getPermValue(), seasonId);
			
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

}
