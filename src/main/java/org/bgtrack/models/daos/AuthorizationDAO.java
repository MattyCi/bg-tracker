package org.bgtrack.models.daos;

import java.util.List;

import org.bgtrack.models.user.Reguser;
import org.bgtrack.models.user.authorization.Permission;
import org.bgtrack.models.user.authorization.UserPermission;
import org.bgtrack.utils.HibernateUtil;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationDAO {
	
	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationDAO.class);
	
	public static Permission getPermissionByValue(String permissionValue) {
		Session session = BaseDAO.getCurrentSession();
		
		Permission permission = null;
		
		try {
			
			String query = "from Permission where PERM_VALUE=:permissionValue";
			
			permission = (Permission) session.createQuery(query).setParameter("permissionValue", permissionValue).uniqueResult();
			
			session.getTransaction().commit();
			
		} catch(Exception e) {
			
			session.getTransaction().rollback();
			LOG.error("Unexpected error occurred ", e);
			return null;
			
		} finally {
			session.close();
		}
		
		return permission;
		
	}
	
	public static void deletePermissionForUser(Reguser user, Permission permissionToRevoke) {
		Session session = BaseDAO.getCurrentSession();
		
		List<UserPermission> permissionsForUser = null;
		
		try {
			
			String query = "from UserPermission where USER_ID=:userId and PERM_ID=:permId";
			
			permissionsForUser = (List<UserPermission>) session.createQuery(query).setParameter("userId", user.getUserId())
					.setParameter("permId", permissionToRevoke.getPermId()).list();
			
			session.getTransaction().commit();
			
		} catch(Exception e) {
			
			session.getTransaction().rollback();
			LOG.error("Unexpected error occurred ", e);
			return;
			
		} finally {
			session.close();
		}
		
		if (permissionsForUser == null)
			return;
		
		for (UserPermission userPermission : permissionsForUser) {
			HibernateUtil.deleteEntity(userPermission);
		}
		
	}

	public static List<Permission> getAllPermissionsForSeason(String seasonId) {
		Session session = BaseDAO.getCurrentSession();
		
		List<Permission> permissions = null;
		
		try {
			
			String permissionValue = "season:%:"+seasonId;
			
			String query = "from Permission where PERM_VALUE like :permissionValue";
			
			permissions = (List<Permission>) session.createQuery(query).setParameter("permissionValue", permissionValue).list();
			
			session.getTransaction().commit();
			
		} catch(Exception e) {
			
			session.getTransaction().rollback();
			LOG.error("Unexpected error occurred ", e);
			return null;
			
		} finally {
			session.close();
		}
		
		return permissions;
	}
	
}