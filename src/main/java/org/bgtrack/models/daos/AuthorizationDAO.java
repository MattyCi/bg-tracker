package org.bgtrack.models.daos;

import java.util.List;

import org.bgtrack.models.user.Reguser;
import org.bgtrack.models.user.authorization.Permission;
import org.bgtrack.models.user.authorization.UserPermission;
import org.bgtrack.utils.HibernateUtil;
import org.hibernate.Session;

public class AuthorizationDAO {
	
	public static Permission getPermissionByValue(String permissionValue) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		String query = "from Permission where PERM_VALUE=:permissionValue";
		
		Permission permission = (Permission) session.createQuery(query).setParameter("permissionValue", permissionValue).uniqueResult();
		
		session.getTransaction().commit();
		
		session.close();
		
		return permission;
		
	}
	
	public static void deletePermissionForUser(Reguser user, Permission permissionToRevoke) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		String query = "from UserPermission where USER_ID=:userId and PERM_ID=:permId";
		
		List<UserPermission> permissionsForUser = (List<UserPermission>) session.createQuery(query).setParameter("userId", user.getUserId())
				.setParameter("permId", permissionToRevoke.getPermId()).list();
		
		session.getTransaction().commit();
		
		session.close();
		
		if (permissionsForUser == null)
			return;
		
		for (UserPermission userPermission : permissionsForUser) {
			HibernateUtil.deleteEntity(userPermission);
		}
		
	}
	
}